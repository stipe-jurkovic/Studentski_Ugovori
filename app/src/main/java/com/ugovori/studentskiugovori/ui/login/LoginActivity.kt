package com.ugovori.studentskiugovori.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.MainActivity
import org.koin.java.KoinJavaComponent.inject


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref : SharedPreferences by inject(SharedPreferences::class.java)

        val loginViewModel: LoginViewModel by inject(LoginViewModel::class.java)

        val snackbarHostState = loginViewModel.snackbarHostState
        setContent {
            AppTheme{
                LoginCompose(
                    loginViewModel,
                    sharedPref,
                    snackbarHostState
                )
            }
        }
        supportActionBar?.hide()
        loginViewModel.loginSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


    }

}