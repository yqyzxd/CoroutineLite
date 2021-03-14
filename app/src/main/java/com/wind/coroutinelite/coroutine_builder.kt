package com.wind.coroutinelite

import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 *
 *Created By wind
 *  on 3/14/21
 */
fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> Unit
): Job {


    val completion = StandaloneCoroutine(newCoroutineContext(context))
    block.startCoroutine(completion)
    return completion
}

fun <T> async(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> T
): Deferred<T> {
    val completion = DeferredCoroutine<T>(newCoroutineContext(context))
    block.startCoroutine(completion)
    return completion
}

fun newCoroutineContext(context: CoroutineContext): CoroutineContext {
    val combined = context + CoroutineName("@coroutine")
    return if (combined != Dispatchers.Default &&
        combined[ContinuationInterceptor] == null
    ) {
        combined + Dispatchers.Default
    } else combined
}