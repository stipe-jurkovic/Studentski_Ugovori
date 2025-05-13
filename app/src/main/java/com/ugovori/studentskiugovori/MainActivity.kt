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

    //private lateinit var binding: ActivityMainBinding


    private var username = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)
        val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

        username = sharedPref.getString("username", "") ?: ""
        password = sharedPref.getString("password", "") ?: ""

        if (username == "" || password == "") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {

            setContent {
                val navController = rememberNavController()
                AppTheme {
                    MainCompose(navController, mainViewModel, logout = { logout() })
                }
            }

            /*val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_allContracts, R.id.navigation_calculation
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.md_theme_background)))*/

        }
    }
    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> {
                val sharedPref :SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)
                sharedPref.edit().putString("username", "").apply()
                sharedPref.edit().putString("password", "").apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

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