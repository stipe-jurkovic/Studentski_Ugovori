package com.ugovori.studentskiugovori.utils

import com.ugovori.studentskiugovori.model.dataclasses.Ugovor

sealed class Result {

    sealed class NetworkCallResult<T> : Result() {
        data class Success<T>(val data: T) : NetworkCallResult<T>()
        data class Error<T>(val error: String) : NetworkCallResult<T>()
    }

    sealed class LoginResult : Result() {
        data class Success(val data: Any) : LoginResult()

        data class Refresh(val string: String):LoginResult()
        data class Error(val error: String) : LoginResult()
    }

    sealed class ParseResult : Result() {
        data class Success(val data: MutableList<Ugovor>) : ParseResult()
        data class Error(val error: String) : ParseResult()
    }

    sealed class GenericResult : Result() {
        data class Success(val data: Any) :GenericResult()
        data class Error(val error: String) : GenericResult()
    }

}