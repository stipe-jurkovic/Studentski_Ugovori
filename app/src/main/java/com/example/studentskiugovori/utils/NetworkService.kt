package com.example.studentskiugovori.utils

import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlinx.serialization.json.*

var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

    override fun getAcceptedIssuers(): Array<X509Certificate>? {
        return arrayOf()
    }
}
)

class NetworkService : NetworkServiceInterface {

    lateinit var client: OkHttpClient

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

    private var next = ""
    private var simpleID = ""
    private var SC48 = ""
    private var SAMLResponse = ""
    private var simpleAuth = ""
    private var url = ""

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
        next = response.header("Location")!!
        return if (simpleID != "" && SC48 != "") {
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

        val request1_5 = Request.Builder()
            .url(next)
            .get()
            .build()

        val response1_5 = client.newCall(request1_5).execute()
        val ssoID = response1_5.headers["Set-Cookie"]!!.split(";")[0]

        val request1_6 = Request.Builder()
            .url(response1_5.headers["Location"]!!)
            .get().header("Cookie", ssoID)
            .build()
        val response1_6 = client.newCall(request1_6).execute()
        val doc1_6 = Jsoup.parse(response1_6.body?.string())
        val AuthState = doc1_6.select("input[name=AuthState]").attr("value")

        val formBody1_8 = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("AuthState", AuthState)
            .add("Submit", "")
            .build()
        val request1_8 = Request.Builder()
            .url("https://login.aaiedu.hr/sso/module.php/core/loginuserpass.php?")
            .post(formBody1_8).header("Cookie", ssoID)
            .build()

        val response1_8 = client.newCall(request1_8).execute()
        val doc1_8 = Jsoup.parse(response1_8.body?.string())
        SAMLResponse = doc1_8.select("input[name=SAMLResponse]").attr("value")
        return if (SAMLResponse != "") {
            Result.NetworkCallResult.Success("SAMLResponse got!")
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't get SAMLResponse!")
        }
    }

    override fun sendSAMLToWebsc(): Result.NetworkCallResult<String> {

        val formBody3 = FormBody.Builder()
            .add("SAMLResponse", SAMLResponse)
            .build()

        val request3 = Request.Builder()
            .url("https://websc.scst.hr/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody3).header("Cookie", simpleID)
            .build()

        val response3 = client.newCall(request3).execute()
        val doc3 = Jsoup.parse(response3.body?.string())
        simpleAuth = response3.headers["Set-Cookie"]!!.split(";")[0]

        url = doc3.select("a[id=redirlink]").attr("href")

        return if (simpleAuth != "") {
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

        return if (doc6 != "") {
            Result.NetworkCallResult.Success(doc6.toString())
        } else {
            lastTimeLoggedIn = 0L
            Result.NetworkCallResult.Error("Couldn't get scst ugovori data!")
        }
    }

}