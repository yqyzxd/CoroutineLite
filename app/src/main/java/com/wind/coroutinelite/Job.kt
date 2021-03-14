package com.wind.coroutinelite

import kotlin.coroutines.CoroutineContext

/**
 *
 *Created By wind
 *  on 3/14/21
 */
typealias OnCancel=()->Unit
typealias OnCompleted=()->Unit
interface Job :CoroutineContext.Element{

    companion object Key:CoroutineContext.Key<Job>

    override val key: CoroutineContext.Key<*>
        get() = Job



    val isActive:Boolean

    fun invokeOnCancel(onCancel:OnCancel):Disposable
    fun invokeOnCompleted(onCompleted:OnCompleted):Disposable

    suspend fun join()
    fun cancel()
    fun remove(disposable: Disposable)

}