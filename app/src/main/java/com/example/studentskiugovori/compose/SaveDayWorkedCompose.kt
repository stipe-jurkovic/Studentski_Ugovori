package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn( ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectDay() {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { contentPadding ->
        // Screen content

        BottomSheetScaffold(
            modifier = Modifier.padding(contentPadding),
            sheetContent = {
                if (showBottomSheet) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            showBottomSheet = false
                        }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp, 0.dp, 16.dp, 4.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = { showTimePicker = true },
                                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
                                ) {
                                    Text("Start Time")
                                }
                                Button(
                                    onClick = { showTimePicker = true },
                                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
                                ) {
                                    Text("End Time")
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .padding(30.dp)
                                ) { TimePickerCompose() }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .padding(26.dp, 15.dp, 26.dp, 30.dp)
                                    .fillMaxWidth()
                            ) {
                                Button(onClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                                }) {
                                    Text(text = "Odustani")
                                }
                                Button(onClick = {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                                }) {
                                    Text(text = "Spremi")
                                }
                            }
                        }
                    }
                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            CalcCompose()
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerCompose(initialHour: Int = 6, initialMinute: Int = 0, is24Hour: Boolean = true) {
    val timePickerState = rememberTimePickerState(initialHour, initialMinute, is24Hour)
    TimePicker(state = timePickerState)
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun test() {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }


    Column {
        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
            ) {
                Text("Start Time")
            }
            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
            ) {
                Text("End Time")
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(30.dp)
            ) { TimePickerCompose() }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(26.dp, 15.dp, 26.dp, 30.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text(text = "Odustani")
            }
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text(text = "Spremi")
            }
        }
    }

}