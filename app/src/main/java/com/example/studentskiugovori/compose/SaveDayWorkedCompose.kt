package com.example.studentskiugovori.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.round

@Preview
@Composable
fun ThemeSelectDay() {
    AppTheme {
        CalcWholeCompose()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcWholeCompose() {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var startSelected by remember { mutableStateOf(false) }
    var endSelected by remember { mutableStateOf(false) }
    var datemoney by remember { mutableStateOf(mutableMapOf<LocalDate, Float>()) }
    val timePickerStateStart = rememberTimePickerState()
    val timePickerStateEnd = rememberTimePickerState()
    var selection by remember { mutableStateOf<CalendarDay?>(null) }


    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                })
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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TimePickerCompose(timePickerStateStart)
                                TimePickerCompose(timePickerStateEnd)
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .padding(26.dp, 0.dp, 26.dp, 30.dp)
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
                                            if (selection != null) {
                                                datemoney[selection!!.date] =
                                                    ((timePickerStateEnd.hour + timePickerStateEnd.minute / 60 - timePickerStateStart.hour - timePickerStateStart.minute / 60) * 5.3f)

                                            }
                                        }
                                    }
                                }) {
                                    Text(text = "Spremi" + datemoney.toString())
                                }
                            }
                        }
                    }
                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column {
                selection = CalcCompose(datemoney)
                Column {
                    Text("Ukupna zarada: ")
                    Text("Ukupna zarada ovaj tjedan: ")
                    Text("Ukupna predviđena zarada: ")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerCompose(
    timePickerState: TimePickerState,
    initialHour: Int = 6,
    initialMinute: Int = 0,
    is24Hour: Boolean = true
) {
    TimeInput(state = timePickerState)
}


@Preview
@Composable
fun ThemeTest() {
    AppTheme {
        TimePickerCustom()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerCustom() {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var startSelected by remember { mutableStateOf(false) }
    var endSelected by remember { mutableStateOf(false) }
    val timePickerStateStart = rememberTimePickerState()


    Column {
        Row(
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    showTimePicker = true
                    startSelected = true
                },
                modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
            ) {
                Text("Start Time")
            }
            Button(
                onClick = {
                    showTimePicker = true
                    endSelected = true
                },
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
            TimePickerCompose(timePickerStateStart)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(26.dp, 0.dp, 26.dp, 30.dp)
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