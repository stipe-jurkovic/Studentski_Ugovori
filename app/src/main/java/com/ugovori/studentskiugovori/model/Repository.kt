package com.ugovori.studentskiugovori.model

import android.content.SharedPreferences
import com.ugovori.studentskiugovori.utils.NetworkServiceInterface
import com.ugovori.studentskiugovori.utils.PreferenceHelper.booleanFlow
import com.ugovori.studentskiugovori.utils.PreferenceHelper.set
import com.ugovori.studentskiugovori.utils.Result
import com.ugovori.studentskiugovori.utils.SPKey
import com.ugovori.studentskiugovori.utils.testData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Repository(
    private val networkService: NetworkServiceInterface,
    private val sharedPreferences: SharedPreferences? = null
) {

    private val dataMutex = Mutex()

    val showGithubMessage: Flow<Boolean> =
        sharedPreferences?.booleanFlow(
            SPKey.SHOW_GITHUB_MESSAGE.toString(),
            true
        ) ?: flowOf(false)

    suspend fun hideGithubMessage() {
        sharedPreferences?.let {
            it[SPKey.SHOW_GITHUB_MESSAGE] = false
        }
    }

    suspend fun getData(
        username: String,
        password: String,
        forceLogin: Boolean,
        timeout: Boolean = false
    ): Result.LoginResult = dataMutex.withLock {
        if (username == "demo" && password == "demo184555") {
            return@withLock Result.LoginResult.Success(testData)
        }

        if (username.isEmpty() || password.isEmpty()) {
            return@withLock Result.LoginResult.Error("Username or password is empty")
        }

        try {
            if (shouldLogin(forceLogin)) {
                performLogin(username, password)?.let {
                    return@withLock it
                }
            }

            if (
                timeout &&
                System.currentTimeMillis() - networkService.lastTimeGotData <
                DATA_REFRESH_TIMEOUT_MS
            ) {
                return@withLock Result.LoginResult.Refresh(
                    "Data fresh enough, not refreshing"
                )
            }

            val ugovoriData = networkService.getUgovoriData().getOr {
                return@withLock Result.LoginResult.Error(
                    "Error getting data: $it"
                )
            }
            if (forceLogin) { networkService. resetLastTimeGotData() }
            Result.LoginResult.Success(ugovoriData)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            networkService.resetLastTimeLoggedIn()
            Result.LoginResult.Error("Error: ${e.message}")
        }
    }

    private fun shouldLogin(forceLogin: Boolean): Boolean {
        val sessionAge =
            System.currentTimeMillis() - networkService.lastTimeLoggedIn

        return sessionAge > LOGIN_SESSION_TIMEOUT_MS || forceLogin
    }

    private fun performLogin(
        username: String,
        password: String
    ): Result.LoginResult.Error? {
        networkService.getSamlRequest().getOr {
            return Result.LoginResult.Error("Error getting SAML request: $it")
        }

        networkService.getSsoRedirect().getOr {
            return Result.LoginResult.Error("Error getting SSO redirect: $it")
        }

        networkService.getLoginPage().getOr {
            return Result.LoginResult.Error("Error getting login page: $it")
        }

        networkService.submitLogin(username, password).getOr {
            return Result.LoginResult.Error("Error submitting login: $it")
        }

        networkService.sendSAMLToWebsc().getOr {
            return Result.LoginResult.Error("Error sending SAML to WebSC: $it")
        }

        networkService.loginFully().getOr {
            return Result.LoginResult.Error("Error completing login: $it")
        }

        return null
    }

    private companion object {
        const val DATA_REFRESH_TIMEOUT_MS = 60_000L
        const val LOGIN_SESSION_TIMEOUT_MS = 3_600_000L
    }
}

private inline fun <T> Result.NetworkCallResult<T>.getOr(
    onFailure: (String) -> Nothing
): T {
    return when (this) {
        is Result.NetworkCallResult.Success -> data
        is Result.NetworkCallResult.Error -> onFailure(error)
    }
}