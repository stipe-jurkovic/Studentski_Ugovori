package com.example.studentskiugovori.ui.calculation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.compose.autoComplete
import com.example.studentskiugovori.model.data.calculateDayEarning
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import com.example.studentskiugovori.utils.Result.GenericResult
import com.kizitonwose.calendar.core.CalendarDay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcWholeCompose() {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }

    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

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
                    val hourlyText = remember { mutableStateOf(mainViewModel.hourlyPay.value.toString()) }

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
                            hourlyText.value = autoComplete(
                                mainViewModel.hourlyPay.observeAsState().value?.map { it.setScale(2).toPlainString() } ?: emptyList(),
                                mainViewModel.hourlyPay.observeAsState().value?.firstOrNull() ?: BigDecimal(5.25)) ?: ""
                            Spacer(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp))
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
                            val context = LocalContext.current

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
                                        try {
                                            when (val result = calculateDayEarning(
                                                selectionDate.date,
                                                timeStartSelected,
                                                timeEndSelected,
                                                hourlyText.value.toBigDecimal()
                                            )) {
                                                is GenericResult.Success ->
                                                    mainViewModel.addDayWorked(
                                                        selectionDate,
                                                        result.data as WorkedHours
                                                    )

                                                is GenericResult.Error -> mainViewModel.addDayWorked(
                                                    selectionDate,
                                                    WorkedHours(
                                                        UUID.randomUUID(),
                                                        selectionDate.date,
                                                        timeStartSelected,
                                                        timeEndSelected,
                                                        0.toBigDecimal(),
                                                        0.toBigDecimal(),
                                                        0.toBigDecimal()
                                                    )
                                                )
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Netočan unos brojeva",
                                                Toast.LENGTH_SHORT
                                            ).show()
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