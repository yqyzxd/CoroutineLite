package com.wind.coroutinelite

/**
 *
 *Created By wind
 *  on 3/14/21
 */
interface Deferred<T>:Job {
    suspend fun await():T
}
