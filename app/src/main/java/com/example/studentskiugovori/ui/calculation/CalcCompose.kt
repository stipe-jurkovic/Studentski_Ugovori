package com.example.studentskiugovori.ui.calculation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.R
import com.example.studentskiugovori.compose.AppTheme
import com.example.studentskiugovori.compose.WorkedItemCompose
import com.example.studentskiugovori.compose.calendarcompose.SimpleCalendarTitle
import com.example.studentskiugovori.compose.calendarcompose.clickable
import com.example.studentskiugovori.compose.calendarcompose.rememberFirstCompletelyVisibleMonth
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.math.MathContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    .replaceFirstChar { it.uppercase() },
            )
        }
    }
}

@Preview
@Composable
fun ThemeCalcCompose() {
    AppTheme {
        CalcCompose()
    }
}


@Composable
fun CalcCompose(): CalendarDay? {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)
    val daysWorked = mainViewModel.daysWorked.collectAsState().value


    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    val daysOfWeek = daysOfWeek() // Available in the library
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)
    LaunchedEffect(visibleMonth) {
        selection = CalendarDay(LocalDate.now(), DayPosition.MonthDate)
    }// Clear selection if we scroll to a new month.
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())

    ) {
        SimpleCalendarTitle(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            currentMonth = visibleMonth.yearMonth,
            goToPrevious = {
                coroutineScope.launch {
                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth) //could be animated
                }
            },
            goToNext = {
                coroutineScope.launch {
                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                }
            },
        )
        HorizontalCalendar(state = state, dayContent = { day ->
            Day(day, isSelected = selection == day) { clicked ->
                selection = if (clicked == selection) {
                    null
                } else {
                    clicked
                }
            }
        }, userScrollEnabled = false, monthHeader = {
            DaysOfWeekTitle(daysOfWeek = daysOfWeek) // Use the title as month header
        })
        HorizontalDivider()
        Column(modifier = Modifier.fillMaxWidth()) {
            daysWorked[selection?.date]?.toList()?.forEach {
                WorkedItemCompose(it)
            }
        }
        var sum = BigDecimal(0)
        var hours = BigDecimal(0)
        mainViewModel.totalPerDay.collectAsState().value.forEach {
            if (it.key.month == visibleMonth.yearMonth.month) {
                sum += it.value
            }
        }
        mainViewModel.daysWorked.collectAsState().value.forEach { it ->
            if (it.key.month == visibleMonth.yearMonth.month) {
                it.value.forEach { it2 ->
                    hours += it2.hours
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            val rowmodifier = Modifier
                .padding(10.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(10.dp)
            Row(rowmodifier) {
                Text("Ukupna zarada ovaj mjesec: $sum €")

            }
            Row(rowmodifier) {
                Text("Broj odrađenih sati u ${numToMonth(currentMonth.monthValue)}: $hours")
            }
            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(10.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.download_minimalistic_svgrepo_com),
                        contentDescription = "Sati u txt formatu",
                        Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                    Text("Sati u txt formatu")
                }
            }
        }

    }
    return selection

}


fun numToMonth(num: Int): String {
    return when (num) {
        1 -> "siječnju"
        2 -> "veljači"
        3 -> "ožujku"
        4 -> "travnju"
        5 -> "svibnju"
        6 -> "lipnju"
        7 -> "srpnju"
        8 -> "kolovozu"
        9 -> "rujnu"
        10 -> "listopadu"
        11 -> "studenom"
        12 -> "prosincu"
        else -> "mjesecu"
    }
}

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    colors: List<Color> = emptyList(),
    onClick: (CalendarDay) -> Unit = {},
) {
    val selectedItemColor = MaterialTheme.colorScheme.secondary
    val inActiveTextColor = Color.Gray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
    }
    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)
    val daysWorked = mainViewModel.totalPerDay.collectAsState()

    Column(
        modifier = Modifier
            .aspectRatio(1f)  // This is important for square sizing!
            .padding(2.dp)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) selectedItemColor else Color.Transparent,
            )
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
            .clickable { onClick(day) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 4.dp, top = 2.dp)
        )

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (day.position) {
                DayPosition.MonthDate -> if (daysWorked.value[day.date] != null) {
                    Text(
                        text = (daysWorked.value[day.date]?.round(MathContext(3)).toString() + " €")
                            ?: ""
                    )
                } else {
                    Text(text = "")
                }

                else -> Text(text = "")
            }
        }
    }
}