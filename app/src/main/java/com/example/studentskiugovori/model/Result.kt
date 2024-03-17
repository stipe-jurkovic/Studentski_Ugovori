package com.example.studentskiugovori.model

class Result{

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val exception: String) : Result<Nothing>()
    }
}
