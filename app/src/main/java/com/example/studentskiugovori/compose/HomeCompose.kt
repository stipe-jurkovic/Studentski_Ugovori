package com.example.studomatisvu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.R
import com.example.studentskiugovori.compose.CardCompose
import com.example.studentskiugovori.compose.CircularIndicator
import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.ui.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeCompose(homeViewModel: HomeViewModel) {

    val loadedTxt = homeViewModel.loadedTxt.observeAsState().value
    val snackbarHostState = remember { homeViewModel.snackbarHostState }
    val isRefreshing = homeViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = isRefreshing?.let {
        rememberPullRefreshState(it, {
            homeViewModel.getData(true)
        })
    }
    val ugovori = homeViewModel.ugovori.observeAsState().value

    pullRefreshState?.let {
        Modifier
            .pullRefresh(it)
            .padding(0.dp)
            .background(color = colorResource(id = R.color.md_theme_background))
    }?.let { it ->
        Scaffold(
            modifier = it,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->

            if ((loadedTxt == "fetching" || loadedTxt == "unset") && !isRefreshing) {
                CircularIndicator()
            }

            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .background(color = colorResource(id = R.color.md_theme_background))
                    .fillMaxSize(),
            ) {
                if (!ugovori.isNullOrEmpty()) {
                    PullRefreshIndicator(
                        isRefreshing, pullRefreshState, Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(2f)
                    )
                    Column(
                        modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 0.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        CardCompose(homeViewModel.cardData.value ?: CardData())
                        Row {
                            Text(
                                text = "Generirano: ${homeViewModel.generated.value ?: ""}",
                                Modifier.padding(8.dp, 4.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(8.dp, 4.dp))
                        Row {
                            Text(
                                text = "Izdani ugovori: ",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp, 4.dp)
                            )
                        }
                        ugovori.forEach {
                            if (it.STATUSNAZIV?.contains("Izdan") == true) {
                                UgovorCompose(ugovor = it)
                            }
                        }
                        Row {
                            Text(
                                text = "Zadnji isplaćeni ugovor: ",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp, 4.dp)
                            )
                        }
                        val zadnjiIsplacen = ugovori.firstOrNull { it.STATUSNAZIV?.contains("Ispl") == true }
                        if (zadnjiIsplacen != null) {
                            UgovorCompose(ugovor = zadnjiIsplacen)
                        }                    }
                }
            }
        }
    }

}




