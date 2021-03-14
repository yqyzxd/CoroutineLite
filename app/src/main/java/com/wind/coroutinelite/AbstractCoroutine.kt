package com.wind.coroutinelite

import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 *Created By wind
 *  on 3/14/21
 */
abstract class AbstractCoroutine<T>(context: CoroutineContext) : Job, Continuation<T> {

    override val context: CoroutineContext
    val state = AtomicReference<CoroutineState>()

    init {
        state.set(CoroutineState.Incomplete())
        this.context = context + this
    }

    val isCompleted
        get() = state.get() is CoroutineState.Complete<*>
    override val isActive: Boolean
        get() = when (state.get()) {
            is CoroutineState.Complete<*>,
            is CoroutineState.Cancelling -> false
            else -> true
        }

    override fun invokeOnCancel(onCancel: OnCancel): Disposable {
        TODO("Not yet implemented")
    }

    override fun invokeOnCompleted(onCompleted: OnCompleted): Disposable {
        return doOnCompleted { _ ->
            onCompleted()
        }
    }

     fun doOnCompleted(block: (Result<T>) -> Unit): Disposable {
        val disposable = CompletionHandlerDisposable(this, block)

        val newState = state.getAndUpdate {

                prev ->
            when (prev) {
                is CoroutineState.Incomplete -> CoroutineState.Incomplete().from(prev)
                    .with(disposable)
                is CoroutineState.Cancelling -> CoroutineState.Cancelling().from(prev)
                    .with(disposable)
                is CoroutineState.Complete<*> -> prev
            }

        }

        (newState as? CoroutineState.Complete<T>)?.let {
            block(
                when {
                    it.value != null -> Result.success(it.value)
                    it.exception != null -> Result.failure(it.exception)
                    else -> throw IllegalStateException("Error")
                }
            )
        }
        return disposable
    }

    override suspend fun join() {
       when(state.get()){

           is CoroutineState.Incomplete,
               is CoroutineState.Cancelling -> return joinSuspend()
           is CoroutineState.Complete<*> -> return
       }
    }

    private suspend fun joinSuspend()= suspendCoroutine<Unit> {
        continuation ->
        doOnCompleted {
            result ->
            continuation.resume(Unit)
        }

    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun remove(disposable: Disposable) {

        state.updateAndGet { prev ->
            when (prev) {
                is CoroutineState.Incomplete -> {
                    CoroutineState.Incomplete().without(disposable)
                }
                is CoroutineState.Cancelling -> {
                    CoroutineState.Cancelling().without(disposable)

                }
                is CoroutineState.Complete<*> -> {
                    prev
                }

            }
        }
    }

    override fun resumeWith(result: Result<T>) {

        val newState=state.updateAndGet { prev ->
            when (prev) {
                is CoroutineState.Cancelling,
                is CoroutineState.Incomplete -> {
                    CoroutineState.Complete(result.getOrNull(),result.exceptionOrNull()).from(prev)
                }
                is CoroutineState.Complete<*> -> {
                    throw IllegalStateException("Aleady completed")
                }
            }
        }

        newState.notifyCompletion(result)
        newState.clear()

    }


}