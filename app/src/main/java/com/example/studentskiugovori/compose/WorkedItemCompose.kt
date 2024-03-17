package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.model.dataclasses.DayWorked
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun WorkedItemCompose(
    dayWorked: DayWorked = DayWorked(
        LocalDate.now(),
        LocalTime.now(),
        LocalTime.now(),
        BigDecimal(10),
        BigDecimal(10),
        true
    )
) {
    val textModifier = Modifier.padding(10.dp)
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        VerticalDivider(color = Color.Blue, thickness = 5.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = dayWorked.date.toString(), textModifier)
            VerticalDivider()
            Text(
                text = "${dayWorked.timeStart.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${
                          dayWorked.timeEnd.format(DateTimeFormatter.ofPattern("HH:mm"))
                }", textModifier
            )
            VerticalDivider()
            Text(text = dayWorked.hours.toString(), textModifier)
            VerticalDivider()
            Text(text = dayWorked.moneyEarned.toString() + " €", textModifier)
        }
    }
}