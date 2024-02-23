package com.example.studentskiugovori.modules

import com.example.studentskiugovori.model.Repository
import com.example.studentskiugovori.ui.home.HomeViewModel
import com.example.studentskiugovori.ui.login.LoginViewModel
import com.example.studentskiugovori.utils.NetworkService
import com.example.studentskiugovori.utils.NetworkServiceInterface
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    single<NetworkServiceInterface> { NetworkService() }
    single { Repository(get()) }
    single { LoginViewModel(get()) }
    single { HomeViewModel(get(), androidContext()) }
}
