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

/*
package com.example.studentskiugovori

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.compose.setContent
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.security.crypto.MasterKeys
import com.example.studentskiugovori.databinding.ActivityMainBinding
import com.example.studentskiugovori.modules.appModule
import com.example.studentskiugovori.ui.login.LoginActivity
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var username = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activity main xml needs fixing

        val sharedPref: SharedPreferences by KoinJavaComponent.inject(SharedPreferences::class.java)

        username = sharedPref.getString("username", "") ?: ""
        password = sharedPref.getString("password", "") ?: ""

        if (username == "" || password == "") {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {
                MainActCompose()
            }
        }
    }

}

@Preview
@Composable
fun MainActCompose(){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BottomNavigation()
        /*val navController = rememberNavController()
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val items = listOf("firstscreen", "secondscreen")
            items.forEach { screen ->
                Button(
                    onClick = {
                        navController.navigate(screen)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically).offset(y = 100.dp)
                ) {
                    Text(text = screen)
                }
            }
        }
        NavHost(
            navController = navController,
            startDestination = "firstscreen"
        ) {
            composable("firstscreen") {
                FirstScreen(navController)
            }
            composable("secondscreen") {
                SecondScreen(navController)
            }
        }*/
    }
}

@Composable
fun BottomNavigation() {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.List,
        BottomNavItem.Analytics,
        BottomNavItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item
            )
        }
    }
}
sealed class BottomNavItem(
    var title: String,
    var icon: Int
) {
    object Home :
        BottomNavItem(
            "Home",
            R.drawable.ic_chevron_left
        )

    object List :
        BottomNavItem(
            "List",
            R.drawable.ic_chevron_left
        )

    object Analytics :
        BottomNavItem(
            "Analytics",
            R.drawable.ic_chevron_left
        )

    object Profile :
        BottomNavItem(
            "Profile",
            R.drawable.ic_chevron_left
        )
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem
) {
    NavigationBarItem(
        // Text that shows bellow the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },

        // Display if the icon it is select or not
        selected = true,

        // Always show the label bellow the icon or not
        alwaysShowLabel = true,

        // Click listener for the icon
        onClick = { /*TODO*/ },

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors()
    )
}


@Composable
fun FirstScreen(navController: NavController) {
    val msg = navController.currentBackStackEntry?.savedStateHandle?.get<String>("msg")
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("secondscreen") }) {
            Text("Go to next screen")
        }
        Spacer(modifier = Modifier.height(8.dp))
        msg?.let {
            Text(it)
        }
    }
}

@Composable
fun SecondScreen(navController: NavController) {
    var text by remember {
        mutableStateOf("")
    }
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = text, onValueChange = { text = it },
            placeholder = {
                Text("Enter text")
            })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            navController.previousBackStackEntry?.savedStateHandle?.set("msg", text)
            navController.popBackStack()
        }) {
            Text(text = "Submit")
        }
    }
}*/
