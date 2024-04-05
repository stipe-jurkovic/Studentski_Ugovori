package com.example.studentskiugovori.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.Status
import com.example.studomatisvu.compose.UgovorCompose

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullListCompose(mainViewModel: MainViewModel) {

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
    }?.let {
        Scaffold(
            modifier = it,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->

            if ((loadedTxt == Status.FETCHING || loadedTxt == Status.UNSET) && !isRefreshing) {
                CircularIndicator()
            }

            Box(
                modifier = Modifier
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
                                text = "Generirano: ${mainViewModel.generated.value ?: ""}",
                                Modifier.padding(8.dp, 4.dp)
                            )
                        }
                        LazyColumn {
                            items(ugovori.size) { index ->
                                UgovorCompose(ugovor = ugovori[index])
                            }
                        }
                    }
                }
            }
        }
    }
}
