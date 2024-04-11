package com.ugovori.studentskiugovori.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.model.Repository
import com.ugovori.studentskiugovori.ui.login.LoginViewModel
import com.ugovori.studentskiugovori.utils.NetworkService
import com.ugovori.studentskiugovori.utils.NetworkServiceInterface
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single<NetworkServiceInterface> { NetworkService() }
    single { Repository(get()) }
    single { LoginViewModel(get()) }
    single { MainViewModel(get()) }
    single { createSharedPreferences( androidContext() ) }
}
fun createSharedPreferences( applicationContext : Context): SharedPreferences {

    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences.create(
        "PreferencesFilename",
        masterKeyAlias,
        applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}