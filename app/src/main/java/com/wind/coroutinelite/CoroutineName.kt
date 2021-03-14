package com.wind.coroutinelite

import kotlin.coroutines.CoroutineContext

/**
 *
 *Created By wind
 *  on 3/14/21
 */
class CoroutineName(val name:String):CoroutineContext.Element {
    companion object Key:CoroutineContext.Key<CoroutineName>
    override val key= Key

    override fun toString(): String {
        return name
    }
}