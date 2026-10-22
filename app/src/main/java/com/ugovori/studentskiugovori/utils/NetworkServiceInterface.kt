package com.ugovori.studentskiugovori.utils

interface NetworkServiceInterface {

    val lastTimeLoggedIn: Long

    val lastTimeGotData: Long

    fun resetLastTimeLoggedIn()

    fun resetLastTimeGotData()

    fun getSamlRequest(): Result.NetworkCallResult<String>

    fun getSsoRedirect(): Result.NetworkCallResult<String>

    fun getLoginPage(): Result.NetworkCallResult<String>

    fun submitLogin(username: String, password: String): Result.NetworkCallResult<String>

    fun sendSAMLToWebsc(): Result.NetworkCallResult<String>

    fun loginFully(): Result.NetworkCallResult<String>

    fun getUgovoriData(): Result.NetworkCallResult<String>
}