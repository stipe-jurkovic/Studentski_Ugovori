package com.example.studentskiugovori.application

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.modules.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class StudentskiUgovori : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@StudentskiUgovori)
            modules(appModule)
        }
        /*val sharedPref = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""
        val password = sharedPref.getString("password", "") ?: ""
        if (username == "" || password == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
        }*/
    }

    fun createSharedPreferences(): SharedPreferences {

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            "PreferencesFilename",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}