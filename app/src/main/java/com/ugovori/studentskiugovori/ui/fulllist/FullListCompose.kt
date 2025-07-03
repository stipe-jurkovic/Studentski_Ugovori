package com.ugovori.studentskiugovori.ui.fulllist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.R
import com.ugovori.studentskiugovori.Status
import com.ugovori.studentskiugovori.compose.CircularIndicator
import com.ugovori.studomatisvu.compose.UgovorCompose

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullListCompose(mainViewModel: MainViewModel) {

    val loadedTxt = mainViewModel.loadedTxt.observeAsState().value
    val snackbarHostState = remember { mainViewModel.snackbarHostState }
    val isRefreshing = mainViewModel.isRefreshing.observeAsState(false).value
    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        mainViewModel.getData(true)
    })
    val ugovori = mainViewModel.ugovori.observeAsState().value
    val error = mainViewModel.errorText.observeAsState().value


    Scaffold(
        modifier = Modifier.pullRefresh(pullRefreshState),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->

        if ((loadedTxt == Status.FETCHING || loadedTxt == Status.UNSET) && !isRefreshing) {
            CircularIndicator()
        }

        Box(
            modifier = Modifier
                .wrapContentHeight()
                .padding(innerPadding),
        ) {
            PullRefreshIndicator(
            isRefreshing, pullRefreshState, Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f)
            )
            if (!ugovori.isNullOrEmpty()) {
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
            } else if (loadedTxt == Status.FETCHING_ERROR) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Icon(
                            painter = painterResource(R.drawable.error_icon),
                            contentDescription = "Error",
                            modifier = Modifier.width(100.dp)
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = "data not found",
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = error ?: "",
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
