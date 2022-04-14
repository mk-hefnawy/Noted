package com.example.noted

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    enum class Type{ONE, TWO;

        override fun toString(): String {
            return this.name
        }}
    @Test
    fun check_type() {
        println(Type.ONE.toString())
    }
}