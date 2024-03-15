package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import java.math.BigDecimal
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
    var datemoney by remember { mutableStateOf(mapOf<LocalDate, BigDecimal>()) }
    AppTheme {
        CalcCompose(datemoney = datemoney)
    }
}


@Composable
fun CalcCompose( datemoney: Map<LocalDate, BigDecimal>): CalendarDay? {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library
    var selection by remember { mutableStateOf<CalendarDay?>(null) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    val daysOfWeek = daysOfWeek() // Available in the library
    val coroutineScope = rememberCoroutineScope()
    val visibleMonth = rememberFirstCompletelyVisibleMonth(state)
    LaunchedEffect(visibleMonth) { selection = null }// Clear selection if we scroll to a new month.
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()) {
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
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(day, isSelected = selection == day, dateMoney = datemoney) { clicked ->
                    if (clicked == selection) {
                        selection = null
                    } else {
                        selection = clicked
                    }
                }
            },
            userScrollEnabled = false,
            monthHeader = {
                DaysOfWeekTitle(daysOfWeek = daysOfWeek) // Use the title as month header
            }
        )
        /*val pageBackgroundColor = Color.White
        Divider(color = pageBackgroundColor)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = flightsInSelectedDate.value) { flight ->
                FlightInformation(flight)
            }
        }*/
    }
    return selection

}

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    colors: List<Color> = emptyList(),
    dateMoney: Map<LocalDate, BigDecimal>,
    onClick: (CalendarDay) -> Unit = {},
) {
    val selectedItemColor = MaterialTheme.colorScheme.secondary
    val inActiveTextColor = Color.Gray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)  // This is important for square sizing!
            .padding(2.dp)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) selectedItemColor else Color.Transparent,
            )
            .background(color = MaterialTheme.colorScheme.tertiaryContainer)
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = day.date.dayOfMonth.toString(), color = textColor)
            var money = dateMoney[day.date]
            val text = if (money != null) {
                if (money > BigDecimal(99))
                { money = money.setScale(1)}
                money.toString() + "€"
            }
            else { "" }
            when (day.position) {
                DayPosition.MonthDate -> Text(text = text ?:"")
                else-> Text(text = "")
            }

        }
    }
}