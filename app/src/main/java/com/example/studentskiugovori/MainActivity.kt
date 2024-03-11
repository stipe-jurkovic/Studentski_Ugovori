package com.example.studentskiugovori

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studentskiugovori.databinding.ActivityMainBinding
import com.example.studentskiugovori.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.java.KoinJavaComponent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var username = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activity main xml needs fixing

        val sharedPref :SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)

        username = sharedPref.getString("username", "") ?: ""
        password = sharedPref.getString("password", "") ?: ""

        if (username == "" || password == "") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_calculation
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.md_theme_background)))

        }
    }
}