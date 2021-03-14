package com.wind.coroutinelite

/**
 *
 *Created By wind
 *  on 3/14/21
 */
sealed class DisposableList {
    object Nil:DisposableList()
    class Cons(val head:Disposable,val tail:DisposableList):DisposableList()
}


fun DisposableList.remove(disposable:Disposable):DisposableList{


    return when(this){
        DisposableList.Nil -> return this

        is DisposableList.Cons -> {
            if (head == disposable){
                tail
            }else{
                DisposableList.Cons(head,tail.remove(disposable))
            }
        }
    }
}

fun DisposableList.forEach(action:(disposable:Disposable)->Unit){
    when(this){
        DisposableList.Nil -> Unit
        is DisposableList.Cons -> {
            action(this.head)
            this.tail.forEach(action)
        }
    }
}

inline fun <reified T:Disposable> DisposableList.loopOn(crossinline action:(T)->Unit)
    = forEach {
        when(it){
            is T -> action(it)
        }
    }