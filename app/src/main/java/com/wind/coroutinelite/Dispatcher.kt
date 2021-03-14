package com.wind.coroutinelite

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

/**
 *
 *Created By wind
 *  on 3/14/21
 */
interface Dispatcher {
    fun dispatch(block: () -> Unit)
}


open class DispatcherContext(private val dispatcher: Dispatcher) :
    AbstractCoroutineContextElement(ContinuationInterceptor),
    ContinuationInterceptor {
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return DispatcherContinuation(continuation,dispatcher)
    }


}
private class DispatcherContinuation<T>(val delegate:Continuation<T>,val dispatcher: Dispatcher)
    :Continuation<T>{
    override val context: CoroutineContext=delegate.context

    override fun resumeWith(result: Result<T>) {
        dispatcher.dispatch {
            delegate.resumeWith(result)
        }
    }

}

object DefaultDispatcher:Dispatcher{
    private val threadGroup=ThreadGroup("DefaultDispatcher")
    private val threadIndex=AtomicInteger(0)

    private val executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1){
        runnable->
            Thread(threadGroup,runnable,"${threadGroup.name}-worker-${threadIndex.getAndIncrement()}").apply { isDaemon=true }

    }
    override fun dispatch(block: () -> Unit) {
        executor.submit(block)
    }
}

object Dispatchers{
    val Default by lazy {
        DispatcherContext(DefaultDispatcher)
    }

    val Android by lazy {
        DispatcherContext(AndroidDispatcher)
    }
}

object AndroidDispatcher:Dispatcher{
    private val handler=Handler(Looper.getMainLooper())
    override fun dispatch(block: () -> Unit) {
        handler.post(block)
    }

}