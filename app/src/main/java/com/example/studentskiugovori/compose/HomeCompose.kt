package com.example.studomatisvu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.R
import com.example.studentskiugovori.Status
import com.example.studentskiugovori.compose.CardCompose
import com.example.studentskiugovori.compose.CircularIndicator
import com.example.studentskiugovori.model.dataclasses.CardData

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeCompose(mainViewModel: MainViewModel) {

    val loadedTxt = mainViewModel.loadedTxt.observeAsState().value
    val snackbarHostState = remember { mainViewModel.snackbarHostState }
    val isRefreshing = mainViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = isRefreshing?.let {
        rememberPullRefreshState(it, {
            mainViewModel.getData(true)
        })
    }
    val ugovori = mainViewModel.ugovori.observeAsState().value

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

            if ((loadedTxt == Status.FETCHING || loadedTxt == Status.UNSET) && !isRefreshing) {
                CircularIndicator()
            }

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                PullRefreshIndicator(
                    isRefreshing, pullRefreshState, Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(2f)
                )
                if (!ugovori.isNullOrEmpty()) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 0.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        item {
                            CardCompose(mainViewModel.cardData.observeAsState().value ?: CardData())
                            Row {
                                Text(
                                    text = "Generirano: ${mainViewModel.generated.value ?: ""}",
                                    Modifier.padding(8.dp, 4.dp)
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(8.dp, 4.dp))
                            if (ugovori.any { it.STATUSNAZIV?.contains("Izdan") == true }) {
                                Row {
                                    Text(
                                        text = "Izdani ugovori: ",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(8.dp, 4.dp)
                                    )
                                }
                            }
                        }
                        items(ugovori.size) { index ->
                            val ugovor = ugovori[index]
                            if (ugovor.STATUSNAZIV?.contains("Izdan") == true) {
                                UgovorCompose(ugovor)
                            }
                        }
                        item {
                            val zadnjiIsplacen =
                                ugovori.firstOrNull { it.STATUSNAZIV?.contains("Ispl") == true }
                            if (zadnjiIsplacen != null) {
                                Text(
                                    text = "Zadnji isplaćeni ugovor: ",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(8.dp, 4.dp)
                                )
                                UgovorCompose(ugovor = zadnjiIsplacen)
                            }
                        }
                    }

                }
            }
        }
    }

}




