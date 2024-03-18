package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.model.Result.Result
import com.example.studentskiugovori.model.data.calculateDayEarning
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import com.example.studentskiugovori.ui.home.HomeViewModel
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.time.LocalTime


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
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val homeViewModel: HomeViewModel by KoinJavaComponent.inject(HomeViewModel::class.java)

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Dodaj") },
                icon = { Icon(Icons.Filled.Edit, contentDescription = "") },
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
                    val daysWorked = homeViewModel.daysWorked.collectAsState().value
                    var timeStart = LocalTime.of(6, 0)
                    var timeEnd = LocalTime.of(14, 0)
                    if (!daysWorked[selection?.date].isNullOrEmpty()) {
                        timeStart = daysWorked[selection?.date]?.first()?.timeStart
                        timeEnd = daysWorked[selection?.date]?.first()?.timeEnd
                    }
                    val timePickerStateStart = rememberTimePickerState(
                        timeStart?.hour ?: 6,
                        timeStart?.minute ?: 0
                    )
                    val timePickerStateEnd = rememberTimePickerState(
                        timeEnd?.hour ?: 14,
                        timeEnd?.minute ?: 0
                    )
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
                                TimeInput(timePickerStateStart)
                                TimeInput(timePickerStateEnd)
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
                                                val selectionDate = selection as CalendarDay
                                                val workedHours: WorkedHours =
                                                    when (val result = calculateDayEarning(
                                                        selectionDate.date,
                                                        LocalTime.of(
                                                            timePickerStateStart.hour,
                                                            timePickerStateStart.minute
                                                        ),
                                                        LocalTime.of(
                                                            timePickerStateEnd.hour,
                                                            timePickerStateEnd.minute
                                                        ),
                                                        5.3.toBigDecimal()
                                                    )) {
                                                        is Result.Success -> result.data
                                                        is Result.Error -> WorkedHours(
                                                            selectionDate.date,
                                                            LocalTime.of(
                                                                timePickerStateStart.hour,
                                                                timePickerStateStart.minute
                                                            ),
                                                            LocalTime.of(
                                                                timePickerStateEnd.hour,
                                                                timePickerStateEnd.minute
                                                            ),
                                                            0.toBigDecimal(),
                                                            0.toBigDecimal()
                                                        )
                                                    }
                                                homeViewModel.addDayWorked(
                                                    selectionDate,
                                                    workedHours
                                                )
                                            }
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
            Column {
                selection = CalcCompose()
                var sum = BigDecimal(0)
                homeViewModel.totalPerDay.collectAsState().value.forEach {
                    sum += it.value
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .padding(10.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) { Text("Ukupna zarada: $sum €") }
                    /*Text("Ukupna zarada ovaj tjedan: ")
                    Text("Ukupna predviđena zarada: ")*/
                }
            }
        }
    }
}

@Preview
@Composable
fun AnaliticCompose() {
    AppTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(10.dp)
            ) { Text("Ukupna zarada: 10 €") }
            /*Text("Ukupna zarada ovaj tjedan: ")
            Text("Ukupna predviđena zarada: ")*/
        }
    }
}