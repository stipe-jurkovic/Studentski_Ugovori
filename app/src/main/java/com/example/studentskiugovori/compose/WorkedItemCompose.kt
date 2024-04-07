package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentskiugovori.MainViewModel
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Preview
@Composable
fun WorkedItemCompose(
    workedHours: WorkedHours = WorkedHours(
        UUID.randomUUID(),
        LocalDate.now(),
        LocalTime.now(),
        LocalTime.now(),
        BigDecimal(10),
        BigDecimal(10),
        BigDecimal(10),
        true
    )
) {
    val textModifier = Modifier.padding(10.dp)
    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        VerticalDivider(color = Color.Blue, thickness = 3.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = workedHours.date.format(DateTimeFormatter.ofPattern("dd.MM")), textModifier)
            VerticalDivider()
            Text(
                text = "${workedHours.timeStart.format(DateTimeFormatter.ofPattern("H:mm"))} - ${
                          workedHours.timeEnd.format(DateTimeFormatter.ofPattern("H:mm"))
                }", textModifier
            )
            VerticalDivider()
            Text(text = workedHours.hours.toString(), textModifier)
            VerticalDivider()
            Text(text = workedHours.hourlyPay.toString() + " €", textModifier)
            VerticalDivider()
            Text(text = workedHours.moneyEarned.toString() + " €", textModifier)
            Button(onClick = { mainViewModel.deleteWorkedItem(workedHours) },
                modifier = Modifier
                    .wrapContentWidth()
                    .defaultMinSize(minWidth = 2.dp),
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary)){
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")
            }
        }
    }
}