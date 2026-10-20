package com.ugovori.studentskiugovori.utils

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class NetworkService : NetworkServiceInterface {

    companion object {
        private const val TAG = "NetworkService"

        private const val BASE_URL = "https://websc.scst.hr"
        private const val LOGIN_URL = "$BASE_URL/sc/48/login/aai"
        private const val SAML_ACS_URL =
            "$BASE_URL/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp"
        private const val DATA_URL =
            "$BASE_URL/sc/48/ugovor/pregled/0?reqtype=kendogrid&pageSize=10000"
    }


    private val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) = Unit

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) = Unit
        }
    )

    private val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(
            sslContext.socketFactory,
            trustAllCerts[0] as X509TrustManager
        )
        .hostnameVerifier(HostnameVerifier { _, _ -> true })
        .followRedirects(false)
        .followSslRedirects(false)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    override var lastTimeLoggedIn = 0L
    override var lastTimeGotData = 0L

    private var simpleAuth = ""
    private var simpleID = ""
    private var sc48 = ""

    private var nextUrl = ""
    private var webscRedirectUrl = ""
    private var ssoCookie = ""
    private var authUrl = ""
    private var authState = ""
    private var samlResponse = ""

    override fun resetLastTimeLoggedIn() {
        lastTimeLoggedIn = 0L
    }

    override fun resetLastTimeGotData() {
        lastTimeGotData = 0L
    }

    /**
     * Starts the authentication flow and retrieves the initial cookies
     * and redirect URL.
     */
    override fun getSamlRequest(): Result.NetworkCallResult<String> {
        clearLoginState()

        val request = Request.Builder()
            .url(LOGIN_URL)
            .get()
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code

            for (cookie in response.headers.values("Set-Cookie")) {
                val pair = cookie.substringBefore(";")

                when {
                    pair.startsWith("SimpleSAML", ignoreCase = true) ->
                        simpleID = pair

                    pair.startsWith("SC48", ignoreCase = true) ->
                        sc48 = pair
                }
            }

            val location = response.header("Location")
            nextUrl = location
                ?.let { response.request.url.resolve(it)?.toString() }
                .orEmpty()

            Log.d(TAG, "Initial SAML request: HTTP $code")

            if (
                code == 303 &&
                simpleID.isNotEmpty() &&
                sc48.isNotEmpty() &&
                nextUrl.isNotEmpty()
            ) {
                Result.NetworkCallResult.Success("SAML request obtained")
            } else {
                Result.NetworkCallResult.Error(
                    "Initial authentication request failed (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Error) {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Follows the initial redirect and obtains the SSO cookie and
     * authentication URL.
     */
    override fun getSsoRedirect(): Result.NetworkCallResult<String> {
        if (nextUrl.isBlank()) {
            return failure("Missing initial SAML redirect URL")
        }

        val request = Request.Builder()
            .url(nextUrl)
            .get()
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code

            ssoCookie = response.headers.values("Set-Cookie")
                .firstOrNull()
                ?.substringBefore(";")
                .orEmpty()

            val location = response.header("Location")
            authUrl = location
                ?.let { response.request.url.resolve(it)?.toString() }
                .orEmpty()

            Log.d(TAG, "SSO redirect: HTTP $code")

            if (
                code in listOf(302, 303) &&
                ssoCookie.isNotBlank() &&
                authUrl.isNotBlank()
            ) {
                Result.NetworkCallResult.Success("SSO redirect obtained")
            } else {
                Result.NetworkCallResult.Error(
                    "SSO redirect failed (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Error) {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Loads the login page and extracts AuthState from the form action URL.
     */
    override fun getLoginPage(): Result.NetworkCallResult<String> {
        if (authUrl.isBlank() || ssoCookie.isBlank()) {
            return failure("Missing SSO authentication state")
        }

        val request = Request.Builder()
            .url(authUrl)
            .get()
            .header("Cookie", ssoCookie)
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code
            val html = response.body?.string().orEmpty()
            val document = Jsoup.parse(html)

            val action = document
                .selectFirst("form.login-form")
                ?.attr("action")
                .orEmpty()

            // Resolve relative form actions against the actual response URL.
            val formUrl = if (action.isNotBlank()) {
                response.request.url.resolve(action)
            } else {
                null
            }

            authState = formUrl?.queryParameter("AuthState").orEmpty()

            // Preserve the form's action URL for the login POST.
            if (formUrl != null) {
                authUrl = formUrl.toString()
            }

            Log.d(TAG, "Login page: HTTP $code")

            if (code == 200 && authState.isNotBlank()) {
                Result.NetworkCallResult.Success("Login page obtained")
            } else {
                Result.NetworkCallResult.Error(
                    "Could not obtain login form or AuthState (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Error) {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Submits the user's credentials and extracts the SAML response.
     */
    override fun submitLogin(
        username: String,
        password: String
    ): Result.NetworkCallResult<String> {
        if (authUrl.isBlank() || authState.isBlank()) {
            return failure("Missing login form state")
        }

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("AuthState", authState)
            .add("Submit", "")
            .build()

        val request = Request.Builder()
            .url(authUrl)
            .post(formBody)
            .header("Cookie", ssoCookie)
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code
            val html = response.body?.string().orEmpty()
            val document = Jsoup.parse(html)

            samlResponse = document
                .selectFirst("input[name=SAMLResponse]")
                ?.attr("value")
                .orEmpty()

            Log.d(TAG, "Login submission: HTTP $code")

            if (code == 200 && samlResponse.isNotBlank()) {
                Result.NetworkCallResult.Success("SAML response obtained")
            } else {
                Result.NetworkCallResult.Error(
                    "Login failed or SAML response was missing (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Error) {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Sends the SAML response to the service provider.
     */
    override fun sendSAMLToWebsc(): Result.NetworkCallResult<String> {
        if (samlResponse.isBlank() || simpleID.isBlank()) {
            return failure("Missing SAML response or session cookie")
        }

        val formBody = FormBody.Builder()
            .add("SAMLResponse", samlResponse)
            .build()

        val request = Request.Builder()
            .url(SAML_ACS_URL)
            .post(formBody)
            .header("Cookie", simpleID)
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code
            val html = response.body?.string().orEmpty()
            val document = Jsoup.parse(html)

            simpleAuth = response.headers.values("Set-Cookie")
                .firstOrNull()
                ?.substringBefore(";")
                .orEmpty()

            val redirectHref = document
                .selectFirst("a#redirlink")
                ?.attr("href")
                .orEmpty()

            webscRedirectUrl = redirectHref
                .takeIf { it.isNotBlank() }
                ?.let { response.request.url.resolve(it)?.toString() }
                .orEmpty()

            Log.d(TAG, "SAML response submission: HTTP $code")

            if (code == 303 && simpleAuth.isNotBlank()) {
                Result.NetworkCallResult.Success("SAML response submitted")
            } else {
                Result.NetworkCallResult.Error(
                    "Could not complete service-provider authentication (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Error) {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Completes authentication with the service provider.
     */
    override fun loginFully(): Result.NetworkCallResult<String> {
        if (webscRedirectUrl.isBlank()) {
            return failure("Missing service-provider redirect URL")
        }

        val cookies = listOf(simpleID, sc48, simpleAuth)
            .filter { it.isNotBlank() }
            .joinToString("; ")

        val request = Request.Builder()
            .url(webscRedirectUrl)
            .get()
            .header("Cookie", cookies)
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code

            Log.d(TAG, "Final login redirect: HTTP $code")

            if (code == 302) {
                Result.NetworkCallResult.Success("Logged in")
            } else {
                Result.NetworkCallResult.Error(
                    "Final authentication redirect failed (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Success) {
            lastTimeLoggedIn = System.currentTimeMillis()
        } else {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Fetches the user's contracts.
     */
    override fun getUgovoriData(): Result.NetworkCallResult<String> {
        val cookies = listOf(simpleID, sc48, simpleAuth)
            .filter { it.isNotBlank() }
            .joinToString("; ")

        if (cookies.isBlank()) {
            return failure("Missing session cookies")
        }

        val request = Request.Builder()
            .url(DATA_URL)
            .get()
            .header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie", cookies)
            .build()

        val result = client.newCall(request).execute().use { response ->
            val code = response.code
            val body = response.body?.string().orEmpty()

            Log.d(TAG, "Contracts request: HTTP $code")

            if (code == 200 && body.isNotBlank()) {
                Result.NetworkCallResult.Success(body)
            } else {
                Result.NetworkCallResult.Error(
                    "Could not fetch contracts (HTTP $code)"
                )
            }
        }

        if (result is Result.NetworkCallResult.Success) {
            lastTimeGotData = System.currentTimeMillis()
        } else {
            resetLastTimeLoggedIn()
        }

        return result
    }

    /**
     * Clears intermediate authentication state before starting a new login.
     */
    private fun clearLoginState() {
        simpleAuth = ""
        simpleID = ""
        sc48 = ""
        nextUrl = ""
        webscRedirectUrl = ""
        ssoCookie = ""
        authUrl = ""
        authState = ""
        samlResponse = ""
    }

    private fun failure(
        message: String
    ): Result.NetworkCallResult.Error<String> {
        resetLastTimeLoggedIn()
        Log.w(TAG, message)
        return Result.NetworkCallResult.Error(message)
    }
}