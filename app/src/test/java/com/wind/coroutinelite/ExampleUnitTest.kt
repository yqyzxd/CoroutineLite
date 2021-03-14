package com.wind.coroutinelite

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun testLaunch(){
        launch(Dispatchers.Default) {
            println(1)
           // delay(1000)
            println(Thread.currentThread().name)
        }

        val deferred=async {
            println("async")
            getValue()
        }
        //val result=deferred.await()
        //println(result)
    }
    suspend fun getValue():String{
        return "HelloWorld"
    }
}