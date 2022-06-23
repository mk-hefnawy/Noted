package com.example.noted.core

sealed class Result<T> {
     class Success<T>(val value: T?): Result<T>()
     class Failure<T>(val throwable: Throwable): Result<T>()
}