package com.example.studentskiugovori.utils


import org.jsoup.nodes.Document

interface NetworkServiceInterface {

        val lastTimeLoggedIn: Long

        fun getSamlRequest(): Result.NetworkCallResult<String>

        fun getSamlResponse(username: String, password: String): Result.NetworkCallResult<String>

        fun sendSAMLToWebsc(): Result.NetworkCallResult<String>

        fun loginFully(): Result.NetworkCallResult<String>

        fun getUgovoriData(): Result.NetworkCallResult<String>
}