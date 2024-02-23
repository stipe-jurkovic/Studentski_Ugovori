package com.example.studentskiugovori.utils

sealed class Result {

    sealed class NetworkCallResult<T> : Result() {
        data class Success<T>(val data: T) : NetworkCallResult<T>(){}
        data class Error<T>(val error: String) : NetworkCallResult<T>()
    }

    sealed class LoginResult : Result() {
        data class Success(val data: Any) : LoginResult(){}
        data class Error(val error: String) : LoginResult()
    }

}