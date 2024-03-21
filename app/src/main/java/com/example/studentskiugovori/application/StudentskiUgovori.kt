package com.example.studentskiugovori.application

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.modules.appModule
import io.realm.Realm
import io.realm.RealmConfiguration
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
        Realm.init(this)
        val  config: RealmConfiguration = RealmConfiguration.Builder().name("kalendarDB") .schemaVersion(1)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(config)
    }
}