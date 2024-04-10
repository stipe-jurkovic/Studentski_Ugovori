package com.example.studentskiugovori.utils

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun getAcceptedIssuers(): Array<X509Certificate>? {
        return arrayOf()
    }
}
)

class NetworkService : NetworkServiceInterface {

    private var client: OkHttpClient

    init {
        val sslContext = SSLContext.getInstance("SSL")

        sslContext.init(null, trustAllCerts, SecureRandom())

        val newBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        newBuilder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        newBuilder.hostnameVerifier { hostname: String?, session: SSLSession? -> true }

        client = newBuilder.followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    override var lastTimeLoggedIn = 0L
    override var lastTimeGotData = 0L
    private var SAMLResponse = ""
    private var simpleAuth = ""
    private var simpleID = ""
    private var SC48 = ""
    private var next = ""
    private var url = ""

    override fun resetLastTimeLoggedIn() {
        lastTimeLoggedIn = 0L
    }

    override fun resetLastTimeGotData() {
        lastTimeGotData = 0L
    }

    override fun getSamlRequest(): Result.NetworkCallResult<String> {

        val request = Request.Builder()
            .url("https://websc.scst.hr:8445/sc/48/login/aai")
            .get()
            .build()

        val response = client.newCall(request).execute()
        val responseHeaders = response.headers.toList()
        for (i in responseHeaders) {
            if (i.first == "Set-Cookie") {
                if (i.second.split(";")[0].contains("simpleSAML")) {
                    simpleID = i.second.split(";")[0]
                }
                if (i.second.split(";")[0].contains("SC48")) {
                    SC48 = i.second.split(";")[0]
                }
            }
        }
        next = response.header("Location").toString()

        println("GetSAMLRequest: ${response.code} ")
        response.close()
        return if (response.code == 302 && simpleID != "" && SC48 != "") {
            Result.NetworkCallResult.Success("SAMLRequest got!")
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't get SAMLRequest!")
        }
    }

    override fun getSamlResponse(
        username: String,
        password: String
    ): Result.NetworkCallResult<String> {

        val request1 = Request.Builder()
            .url(next)
            .get()
            .build()

        val response1 = client.newCall(request1).execute()
        val ssoID = response1.headers["Set-Cookie"]?.split(";")?.get(0) ?: ""

        val url = response1.headers["Location"]

        val request2 = Request.Builder()
            .url(url.toString())
            .get().header("Cookie", ssoID)
            .build()
        val response2 = client.newCall(request2).execute()
        val doc2 = response2.body?.string()?.let { Jsoup.parse(it) }
        //val authState = doc2?.select("input[name=AuthState]")?.attr("value") ?:""
        val loginLink = doc2?.select("form.login-form")?.attr("action").toString()
        val authState = loginLink.split("AuthState=")[1]

        println("GetSAMLResponse1: ${response2.code}")

        response2.close()
        if (response2.code != 200)
            return Result.NetworkCallResult.Error("Couldn't get SAMLResponse!")

        val formBody3 = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("AuthState", authState )
            .add("Submit", "")
            .build()
        val request3 = Request.Builder()
            .url(url.toString())
            .post(formBody3).header("Cookie", ssoID)
            .build()

        val response3 = client.newCall(request3).execute()
        val doc3 = response3.body?.string()?.let { Jsoup.parse(it) }
        SAMLResponse = doc3?.select("input[name=SAMLResponse]")?.attr("value").toString()

        println("GetSAMLResponse2: ${response3.code}")
        response3.close()

        return if (response3.code == 200 && SAMLResponse != "") {
            Result.NetworkCallResult.Success("SAMLResponse got!")
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Login data incorrect. Couldn't get SAMLResponse!")
        }
    }

    override fun sendSAMLToWebsc(): Result.NetworkCallResult<String> {

        val formBody4 = FormBody.Builder()
            .add("SAMLResponse", SAMLResponse)
            .build()

        val request4 = Request.Builder()
            .url("https://websc.scst.hr/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody4).header("Cookie", simpleID)
            .build()

        val response4 = client.newCall(request4).execute()
        val doc4 = response4.body?.string()?.let { Jsoup.parse(it) }

        simpleAuth = response4.headers["Set-Cookie"]?.split(";")?.get(0) ?: ""
        url = doc4?.select("a[id=redirlink]")?.attr("href").toString()

        println("SendSAMLToWebsc: ${response4.code}")

        response4.close()
        return if (response4.code == 303 && simpleAuth != "") {
            Result.NetworkCallResult.Success("SAMLResponse sent to scst!")
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't send SAMLResponse to ISVU!")
        }
    }

    override fun loginFully(): Result.NetworkCallResult<String> {

        val request5 = Request.Builder()
            .url(url).get()
            .header("Cookie", "$simpleID; $SC48; $simpleAuth")
            .build()
        val response5 = client.newCall(request5).execute()

        println("LoginFully: ${response5.code}")

        val doc5 = response5.body?.string()?.let { Jsoup.parse(it) }
        response5.close()

        return if (response5.code == 302) {
            lastTimeLoggedIn = System.currentTimeMillis()
            Result.NetworkCallResult.Success("Logged in!")
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't get ugovori data!")
        }
    }

    override fun getUgovoriData(): Result.NetworkCallResult<String> {
        val baseUrl = "https://websc.scst.hr:8495/sc/48/ugovor/pregled/0"
        val reqType = "kendogrid"
        val panelFilter = """{"datumOd":"","datumDo":"","godina":-1,"racun":0,"status":-2}"""
        val selectedYear = "2023"
        val selectedContract = "22090"
        val take = "999"
        val skip = "0"
        val page = "1"
        val pageSize = "999"
        val sortField1 = "RADIODOWEB"
        val sortDir1 = "desc"
        val sortField2 = "RADIOODWEB"
        val sortDir2 = "desc"

        val url2 =
            "$baseUrl?reqtype=$reqType&panelFilter=$panelFilter&selValues[GODINA]=$selectedYear" +
                    "&selValues[UGOVOR]=$selectedContract&take=$take&skip=$skip&page=$page" +
                    "&pageSize=$pageSize&sort[0][field]=$sortField1&sort[0][dir]=$sortDir1&sort[1][field]=$sortField2" +
                    "&sort[1][dir]=$sortDir2&filter[logic]=and"

        val request6 = Request.Builder()
            .url(url2)
            .get().header("X-Requested-With", "XMLHttpRequest")
            .header("Cookie", "$simpleID; $SC48; $simpleAuth")
            .build()

        val response6 = client.newCall(request6).execute()
        val doc6 = response6.body?.string()

        println("GetUgovoriData: ${response6.code}")
        response6.close()

        return if (response6.code == 200 && doc6 != "") {
            lastTimeGotData = System.currentTimeMillis()
            Result.NetworkCallResult.Success(doc6.toString())
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't get scst ugovori data!")
        }
    }

}