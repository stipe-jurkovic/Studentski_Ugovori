package com.example.studentskiugovori.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
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
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.model.Result.Result
import com.example.studentskiugovori.model.data.calculateDayEarning
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import com.example.studentskiugovori.ui.home.HomeViewModel
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.time.LocalTime
import java.util.UUID


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

        BottomSheetScaffold(
            modifier = Modifier.padding(contentPadding),
            sheetContent = {
                if (showBottomSheet) {
                    val timePickStateStart = rememberTimePickerState(6, 0)
                    val timePickStateEnd = rememberTimePickerState(14, 0)
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = { showBottomSheet = false }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TimeInput(timePickStateStart)
                            TimeInput(timePickStateEnd)
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
                                    val timeStartSelected = LocalTime.of(
                                        timePickStateStart.hour, timePickStateStart.minute
                                    )
                                    val timeEndSelected = LocalTime.of(
                                        timePickStateEnd.hour, timePickStateEnd.minute
                                    )
                                    if (!sheetState.isVisible && selection != null) {
                                        showBottomSheet = false

                                        val selectionDate = selection as CalendarDay
                                        when (val result = calculateDayEarning(
                                            selectionDate.date,
                                            timeStartSelected,
                                            timeEndSelected,
                                            5.3.toBigDecimal()
                                        )) {
                                            is Result.Success ->
                                                homeViewModel.addDayWorked(
                                                    selectionDate,
                                                    result.data
                                                )

                                            is Result.Error -> homeViewModel.addDayWorked(
                                                selectionDate,
                                                WorkedHours(
                                                    UUID.randomUUID(),
                                                    selectionDate.date,
                                                    timeStartSelected,
                                                    timeEndSelected,
                                                    0.toBigDecimal(),
                                                    0.toBigDecimal()
                                                )
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
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column {
                selection = CalcCompose()
            }
        }
    }
}