package com.ugovori.studentskiugovori.ui.calculation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.R
import com.ugovori.studentskiugovori.compose.autoComplete
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalcWholeCompose(mainViewModel: MainViewModel) {

    var showBottomSheet: MutableState<Boolean> = remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }

    fun showSheet(show: Boolean = true) {
        showBottomSheet.value = show
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.add)) },
                icon = { Icon(Icons.Filled.Edit, contentDescription = "") },
                onClick = {
                    showSheet()
                })
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { contentPadding ->

        BottomSheetScaffold(
            modifier = Modifier.padding(contentPadding),
            sheetContent = {
                if (showBottomSheet.value) {
                    AddTimeSheet(
                        mainViewModel = mainViewModel,
                        selection = selection,
                        showSheet = { showSheet(it) }
                    )
                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            Column {
                selection = calcCompose()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddTimeSheet(
    mainViewModel: MainViewModel,
    selection: CalendarDay? = null,
    showSheet:(show:Boolean)-> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val timePickStateStart = rememberTimePickerState(6, 0)
    val timePickStateEnd = rememberTimePickerState(14, 0)
    val hourlyText = remember { mutableStateOf(mainViewModel.hourlyPay.value.toString()) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = { showSheet(false) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TimeInput(timePickStateStart)
            TimeInput(timePickStateEnd)
            hourlyText.value = autoComplete(
                mainViewModel.hourlyPay.observeAsState().value?.map {
                    it.setScale(2).toPlainString()
                } ?: emptyList(),
                mainViewModel.hourlyPay.observeAsState().value?.firstOrNull()
                    ?: BigDecimal(6.06)) ?: ""
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
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
                        showSheet(false)
                    }
                }
            }) {
                Text(text = stringResource(R.string.cancel))
            }

            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showSheet(false)

                        selection?.let { selectionDate ->
                            mainViewModel.tryAddDayWorked(
                                selectionDate = selectionDate,
                                timeEndSelected = LocalTime.of(timePickStateEnd.hour, timePickStateEnd.minute),
                                timeStartSelected = LocalTime.of(
                                    timePickStateStart.hour, timePickStateStart.minute
                                ),
                                hourlyText = hourlyText.value,
                            )

                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}