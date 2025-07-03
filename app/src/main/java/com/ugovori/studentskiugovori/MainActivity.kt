package com.ugovori.studentskiugovori

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.compose.rememberNavController
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.navigation.MainCompose
import com.ugovori.studentskiugovori.ui.login.LoginActivity
import org.koin.java.KoinJavaComponent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)
        val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

        if (sharedPref.getString("username", "") == "" || sharedPref.getString("password", "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {
                AppTheme {
                    MainCompose( rememberNavController(), mainViewModel, logout = { logout() })
                }
            }
        }
    }

    fun logout() {
        val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)
        sharedPref.edit {
            putString("username", "")
            putString("password", "")
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}