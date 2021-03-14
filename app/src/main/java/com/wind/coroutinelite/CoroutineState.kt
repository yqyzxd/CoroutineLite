package com.wind.coroutinelite

/**
 *
 *Created By wind
 *  on 3/14/21
 */
sealed class CoroutineState {

    private var disposableList:DisposableList=DisposableList.Nil
    //未启动状态
    class Incomplete:CoroutineState()
    //取消中
    class Cancelling:CoroutineState()
    //已完成
    class Complete<T>(val value:T?=null,val exception:Throwable?=null):CoroutineState()



    fun from(state:CoroutineState):CoroutineState{
        this.disposableList=state.disposableList
        return this
    }

    fun with(disposable: Disposable):CoroutineState{
        this.disposableList=DisposableList.Cons(disposable,this.disposableList)
        return this
    }

    fun without(disposable: Disposable):CoroutineState{

        this.disposableList= this.disposableList.remove(disposable)
        return this
    }

    fun clear(){
        this.disposableList=DisposableList.Nil
    }

    fun <T> notifyCompletion(result: Result<T>){
        this.disposableList.loopOn<CompletionHandlerDisposable<T>> {
            it.onComplete(result)
        }
    }
}