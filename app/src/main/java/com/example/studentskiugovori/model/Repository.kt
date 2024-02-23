package com.example.studentskiugovori.model

import com.example.studentskiugovori.model.data.parseStudent
import com.example.studentskiugovori.model.data.parseTrenutnuGodinu
import com.example.studentskiugovori.model.data.parseUpisaneGodine
import com.example.studentskiugovori.model.dataclasses.Predmet
import com.example.studentskiugovori.utils.NetworkServiceInterface
import com.example.studentskiugovori.utils.Result
import org.jsoup.nodes.Document

class Repository(private val networkService: NetworkServiceInterface) {

    suspend fun loginUser(
        username: String,
        password: String,
        forceLogin: Boolean
    ): Result.LoginResult {

        if (username == "" || password == "") {
            return Result.LoginResult.Error("Username or password is empty")
        }

        try {
            if ((System.currentTimeMillis() - networkService.lastTimeLoggedIn) > 3600000 || forceLogin) {
                when (val result = networkService.getSamlRequest()) {
                    is Result.NetworkCallResult.Success -> {}
                    is Result.NetworkCallResult.Error -> return Result.LoginResult.Error("Error getting SAMLRequest: ${result.error}")
                }
                when (val result = networkService.getSamlResponse(username, password)) {
                    is Result.NetworkCallResult.Success -> {}
                    is Result.NetworkCallResult.Error -> return Result.LoginResult.Error("Error getting SAMLResponse: ${result.error}")
                }
                when (val result = networkService.sendSAMLToISVU()) {
                    is Result.NetworkCallResult.Success -> {}
                    is Result.NetworkCallResult.Error -> return Result.LoginResult.Error("Error sending SAMLRequest to ISVU: ${result.error}")
                }
            }
            return when (val result = networkService.getStudomatData()) {
                is Result.NetworkCallResult.Success -> {
                    Result.LoginResult.Success(parseStudent(result.data))
                }

                is Result.NetworkCallResult.Error -> Result.LoginResult.Error("Error getting data:${result.error}")
            }

        } catch (e: Exception) {
            return Result.LoginResult.Error("Error: ${e.message}")
        }
    }
}