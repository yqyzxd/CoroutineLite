package com.wind.coroutinelite

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *
 *Created By wind
 *  on 3/14/21
 */

private val executor=Executors.newScheduledThreadPool(1){
    runnable->
    Thread(runnable,"Sche").apply { isDaemon=true }
}

suspend fun delay(time:Long,timeUnit:TimeUnit=TimeUnit.MILLISECONDS){

    if(time<=0){
        return
    }
    suspendCoroutine<Unit> {
        continuation->

        executor.schedule({
            continuation.resume(Unit)
        },time,timeUnit)
    }


}