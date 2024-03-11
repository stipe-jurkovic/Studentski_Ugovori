package com.example.studentskiugovori.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.ui.home.HomeViewModel
import com.example.studentskiugovori.ui.login.LoginViewModel
import com.example.studentskiugovori.utils.NetworkService
import com.example.studentskiugovori.utils.NetworkServiceInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single<NetworkServiceInterface> { NetworkService() }
    single { Repository(get()) }
    single { LoginViewModel(get()) }
    single { HomeViewModel(get(), androidContext()) }
    single { MainViewModel(get(), androidContext()) }
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