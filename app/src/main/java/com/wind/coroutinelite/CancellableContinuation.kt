package com.wind.coroutinelite

import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
/**
 *
 *Created By wind
 *  on 3/15/21
 */
class CancellableContinuation<T>(
    private val continuation: Continuation<T>
) : Continuation<T> by continuation {


    private val state = AtomicReference<CancelState>(CancelState.Incomplete)

    private val decision = AtomicReference<CancelDecision>(CancelDecision.UNDECIDED)

    val isCompleted: Boolean
        get() = when (state.get()) {
            CancelState.Incomplete,
            is CancelState.CancelHandler -> false
            is CancelState.Complete<*>,
            CancelState.Cancelled -> true
        }

    fun invokeOnCancellation(onCancel: OnCancel) {

        val newState = state.updateAndGet { prev ->
            when (prev) {
                CancelState.Incomplete -> CancelState.CancelHandler(onCancel)
                is CancelState.CancelHandler ->
                    throw IllegalStateException("Prohibited.")
                CancelState.Cancelled,
                is CancelState.Complete<*> -> prev
            }
        }

        if (newState is CancelState.Cancelled) {
            onCancel()
        }

    }

    private fun installCancelHandler() {
        if (isCompleted) return
        val parent = continuation.context[Job] ?: return
        parent.invokeOnCancel {
            doCancel()
        }
    }

    private fun doCancel() {
        val prevState=state.getAndUpdate { prev ->
            when (prev) {
                is CancelState.CancelHandler,
                CancelState.Incomplete -> {
                    CancelState.Cancelled
                }
                CancelState.Cancelled,
                is CancelState.Complete<*> -> {
                                prev
                }
            }
        }
        if (prevState is CancelState.CancelHandler){
            prevState.onCancel()
            resumeWithException(CancellationException("Cancelled."))
        }
    }

    fun getResult():Any?{
        installCancelHandler()
        if(decision.compareAndSet(CancelDecision.UNDECIDED,CancelDecision.SUSPENDED))
            return COROUTINE_SUSPENDED

        return when(val currentState=state.get()){
            is CancelState.CancelHandler,
                CancelState.Incomplete ->COROUTINE_SUSPENDED
            is CancelState.Cancelled -> throw CancellationException("cancelled")
            is CancelState.Complete<*> -> {
                (currentState as CancelState.Complete<*>).let {
                    it.exception?.let {throw it  }?:it.value
                }
            }
        }
    }
}