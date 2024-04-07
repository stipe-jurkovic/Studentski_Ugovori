package com.example.studentskiugovori.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.model.Result.Result
import com.example.studentskiugovori.model.data.calculateDayEarning
import com.example.studentskiugovori.model.dataclasses.WorkedHours
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
                    val hourly = remember { mutableStateOf(mainViewModel.hourlyPay.value) }
                    var hourlyText =
                        remember { mutableStateOf(mainViewModel.hourlyPay.value.toString()) }
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
                            TextField(
                                value = hourlyText.value,
                                onValueChange = { hourlyText.value = it },
                                label = { Text("Satnica") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                suffix = { Text("€/h") },
                                modifier = Modifier.widthIn(80.dp)
                            )
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
                                                is Result.Success ->
                                                    mainViewModel.addDayWorked(
                                                        selectionDate,
                                                        result.data
                                                    )

                                                is Result.Error -> mainViewModel.addDayWorked(
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
                                                "Netočan unos satnice",
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Demo_ExposedDropdownMenuBox() {
    val context = LocalContext.current
    val coffeeDrinks = arrayOf("Americano", "Cappuccino", "Espresso", "Latte", "Mocha")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(coffeeDrinks[0]) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                coffeeDrinks.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}