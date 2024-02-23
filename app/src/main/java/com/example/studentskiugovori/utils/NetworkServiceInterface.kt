package com.example.studentskiugovori.utils


import org.jsoup.nodes.Document

interface NetworkServiceInterface {

        val lastTimeLoggedIn: Long

        fun getSamlRequest(): Result.NetworkCallResult<String>

        fun getSamlResponse(username: String, password: String): Result.NetworkCallResult<String>

        fun sendSAMLToISVU(): Result.NetworkCallResult<String>

        fun getStudomatData(): Result.NetworkCallResult<Document>

        fun getStudomatExamsReg(): Result.NetworkCallResult<Document>
}