package com.wind.coroutinelite

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

/**
 *
 *Created By wind
 *  on 3/14/21
 */
class DeferredCoroutine<T>(context:CoroutineContext):AbstractCoroutine<T>(context),Deferred<T> {
    override suspend fun await(): T {
       val currentState= state.get()
       return when(currentState){
           is CoroutineState.Complete<*> ->{
               currentState.exception?.let { throw it }?:(currentState.value as T)
           }
           is CoroutineState.Incomplete,
               is CoroutineState.Cancelling -> awaitSuspend()
       }
    }

    private suspend fun awaitSuspend()= suspendCoroutine<T> {
        continuation ->
        doOnCompleted {
            result ->
            continuation.resumeWith(result)
        }
    }

}