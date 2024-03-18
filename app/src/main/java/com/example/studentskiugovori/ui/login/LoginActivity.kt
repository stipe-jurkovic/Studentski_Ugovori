package com.example.studentskiugovori.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.MainActivity
import com.example.studentskiugovori.R
import com.example.studentskiugovori.compose.LoginCompose
import com.example.studentskiugovori.databinding.ActivityLoginBinding
import org.koin.java.KoinJavaComponent.inject


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        val sharedPref : SharedPreferences by inject(SharedPreferences::class.java)

        val loginViewModel: LoginViewModel by inject(LoginViewModel::class.java)

        setContentView(binding.root)
        val snackbarHostState = loginViewModel.snackbarHostState
        binding.composeViewLogin.setContent {
            AppTheme(){
                LoginCompose(
                    loginViewModel,
                    sharedPref,
                    snackbarHostState
                )
            }
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.md_theme_background)))
        loginViewModel.loginSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


    }

}