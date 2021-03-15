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

    /**
     * 注册取消回调
     */
    fun invokeOnCancel(onCancel:OnCancel):Disposable

    /**
     * 注册完成回调
     */
    fun invokeOnCompleted(onCompleted:OnCompleted):Disposable

    /**
     * 等待协程完成
     */
    suspend fun join()

    /**
     * 取消协程
     */
    fun cancel()

    /**
     * 移除回调
     */
    fun remove(disposable: Disposable)

}