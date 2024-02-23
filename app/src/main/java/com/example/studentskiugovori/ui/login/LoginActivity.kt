package com.example.studentskiugovori.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.MainActivity
import com.example.studentskiugovori.compose.LoginCompose
import com.example.studentskiugovori.databinding.ActivityLoginBinding
import org.koin.java.KoinJavaComponent.inject


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPref = EncryptedSharedPreferences.create(
            "PreferencesFilename",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val loginViewModel: LoginViewModel by inject(LoginViewModel::class.java)

        setContentView(binding.root)
        val snackbarHostState = loginViewModel.snackbarHostState
        binding.composeViewLogin.setContent {
            LoginCompose(
                loginViewModel,
                sharedPref,
                snackbarHostState
            )
        }
        loginViewModel.loginSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }


    }

}