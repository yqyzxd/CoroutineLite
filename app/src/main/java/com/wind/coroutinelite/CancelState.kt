package com.wind.coroutinelite

/**
 *
 *Created By wind
 *  on 3/15/21
 */
sealed class CancelState {

    object Incomplete:CancelState()
    class CancelHandler(val onCancel: OnCancel):CancelState()
    class Complete<T>(val value:T?=null,val exception:Throwable?=null):CancelState()
    object Cancelled:CancelState()


}

enum class CancelDecision{

    UNDECIDED,SUSPENDED,RESUMED
}