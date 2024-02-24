package com.example.studomatisvu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.R
import com.example.studentskiugovori.ui.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeCompose(homeViewModel: HomeViewModel) {

    val loadedTxt = homeViewModel.loadedTxt.observeAsState().value
    val snackbarHostState = remember { homeViewModel.snackbarHostState }
    val isRefreshing = homeViewModel.isRefreshing.observeAsState().value
    val pullRefreshState = isRefreshing?.let { it ->
        rememberPullRefreshState(it, {
                homeViewModel.getData(true)
        })
    }
    val ugovori = homeViewModel.ugovori.observeAsState().value

    pullRefreshState?.let {
        Modifier
            .pullRefresh(it)
            .padding(0.dp)
    }?.let { it ->
        Scaffold(modifier = it,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->

            if ((loadedTxt == "fetching" || loadedTxt == "unset") && !isRefreshing) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .zIndex(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .background(colorResource(id = R.color.white))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .width(64.dp)
                                .height(64.dp)
                                //.offset(0.dp, (-20).dp)
                            ,
                            color = colorResource(id = R.color.StudomatBlue),
                            trackColor = colorResource(id = R.color.StudomatBlueLite),
                            strokeWidth = 4.dp,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .wrapContentHeight()
                    .padding(innerPadding),
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
                        Row {
                            Text(
                                text = "Generirano: ${homeViewModel.generated.value ?: ""}",
                                Modifier.padding(8.dp, 4.dp)
                            )
                        }
                        ugovori.forEach {
                            UgovorCompose(ugovor = it)
                        }
                    }
                }
            }
        }
    }
}
