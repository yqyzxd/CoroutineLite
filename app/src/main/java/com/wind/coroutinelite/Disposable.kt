package com.wind.coroutinelite

/**
 *
 *Created By wind
 *  on 3/14/21
 */
interface Disposable {

    fun dispose()
}

class CompletionHandlerDisposable<T>(
    val  job:Job,val onComplete:(Result<T>)->Unit):Disposable{

    override fun dispose(){
        job.remove(this)
    }

}