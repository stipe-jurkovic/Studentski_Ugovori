package com.ugovori.studentskiugovori.utils


interface NetworkServiceInterface {

        val lastTimeLoggedIn: Long

        val lastTimeGotData: Long

        fun resetLastTimeLoggedIn()

        fun resetLastTimeGotData()

        fun getSamlRequest(): Result.NetworkCallResult<String>

        fun getSamlResponse(username: String, password: String): Result.NetworkCallResult<String>

        fun sendSAMLToWebsc(): Result.NetworkCallResult<String>

        fun loginFully(): Result.NetworkCallResult<String>

        fun getUgovoriData(): Result.NetworkCallResult<String>
}