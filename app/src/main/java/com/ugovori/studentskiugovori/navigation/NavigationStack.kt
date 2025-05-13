package com.ugovori.studentskiugovori.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.R
import com.ugovori.studentskiugovori.compose.AppTheme
import com.ugovori.studentskiugovori.compose.calendarcompose.clickable
import com.ugovori.studentskiugovori.ui.calculation.CalcWholeCompose
import com.ugovori.studentskiugovori.ui.fulllist.FullListCompose
import com.ugovori.studentskiugovori.ui.home.HomeCompose

val topLevelRoutes = listOf(
    TopLevelRoute<FullList>(
        route = FullList,
        iconId = R.drawable.ic_dashboard_black_24dp,
        nameId = R.string.svi_ugovori
    ),
    TopLevelRoute<Home>(
        route = Home,
        iconId = R.drawable.ic_home_black_24dp,
        nameId = R.string.homeText
    ),
    TopLevelRoute<Calculation>(
        route = Calculation,
        iconId = R.drawable.math_svgrepo_com,
        nameId = R.string.izracun
    )
)

@Composable
fun MainCompose(navController: NavHostController, mainViewModel: MainViewModel, logout: () -> Unit) {
    AppTheme {
        Scaffold(
            topBar = { TopBar(navController, logout) },
            bottomBar = { BottomBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                composable<FullList> {
                    FullListCompose(mainViewModel)
                }
                composable<Home> {
                    HomeCompose(mainViewModel)
                }
                composable<Calculation> {
                    CalcWholeCompose()
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    NavigationBar {
        topLevelRoutes.forEach { topLevelRoute ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = topLevelRoute.iconId),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text(stringResource(topLevelRoute.nameId)) },
                selected = false,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, logout: () -> Unit) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination?.route?.split(".")?.lastOrNull() ?: ""
    TopAppBar(
        title = { Text(topLevelRoutes.find {
            it.route.toString() == currentDestination
        }?.nameId?.let { stringResource(it) } ?: "",) },
        actions = {
            Box(Modifier.clickable{ logout() }) {
                Icon(
                    painter = painterResource(R.drawable.logout_line_svgrepo_com),
                    contentDescription = "Odjava",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    )
}