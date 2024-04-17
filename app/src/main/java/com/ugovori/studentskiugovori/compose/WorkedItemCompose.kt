package com.ugovori.studentskiugovori.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.ugovori.studentskiugovori.MainViewModel
import com.ugovori.studentskiugovori.model.dataclasses.WorkedHours
import org.koin.java.KoinJavaComponent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/*@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun WorkedItemCompose(
    workedHours: WorkedHours = WorkedHours(
        UUID.randomUUID(),
        LocalDate.now(),
        LocalTime.now(),
        LocalTime.now(),
        BigDecimal(100),
        BigDecimal(10),
        BigDecimal(10),
        true
    )
) {
    val textModifier = Modifier.padding(10.dp)
    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .wrapContentWidth(unbounded = true)
            .height(IntrinsicSize.Min)
            .background(color = MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalDivider(color = Color.Blue, thickness = 3.dp)

        Text(text = workedHours.date.format(DateTimeFormatter.ofPattern("dd.MM")), textModifier)
        VerticalDivider()
        Text(
            text = "${workedHours.timeStart.format(DateTimeFormatter.ofPattern("H:mm"))} - ${
                workedHours.timeEnd.format(DateTimeFormatter.ofPattern("H:mm"))
            }",
            textModifier
        )
        VerticalDivider()
        Text(text = workedHours.hours.toString(), textModifier)
        VerticalDivider()
        Text(text = workedHours.hourlyPay.toString() + " €", textModifier)
        VerticalDivider()
        Text(
            text = workedHours.moneyEarned.toString() + " €",
            Modifier.padding(start = 10.dp, end = 0.dp)
        )
        Button(
            onClick = { mainViewModel.deleteWorkedItem(workedHours) },
            modifier = Modifier
                .wrapContentWidth()
                .defaultMinSize(minWidth = 2.dp),
            shape = RectangleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")

        }
    }
}*/

@Preview
@Composable
fun ThreeLineListItemWithOverlineAndSupporting(
    workedHours: WorkedHours = WorkedHours(
    UUID.randomUUID(),
    LocalDate.now(),
    LocalTime.now(),
    LocalTime.now(),
    BigDecimal(100),
    BigDecimal(10),
    BigDecimal(10),
    true
)) {
    val mainViewModel: MainViewModel by KoinJavaComponent.inject(MainViewModel::class.java)

    Column {
        ListItem(
            headlineContent = { Text(text = "Zarađeno: " + workedHours.moneyEarned.toString() + " €") },
            overlineContent = {
                Text(
                    text = workedHours.date.format(DateTimeFormatter.ofPattern("dd.MM")) + "  ${
                        workedHours.timeStart.format(
                            DateTimeFormatter.ofPattern("H:mm")
                        )
                    } - ${
                        workedHours.timeEnd.format(DateTimeFormatter.ofPattern("H:mm"))
                    }"
                )
            },
            supportingContent = { Text("Satnica: " + workedHours.hourlyPay.toString() +" €"
            + "  Sati: " + workedHours.hours.toString()) },
            leadingContent = {},
            trailingContent = {
                Button(
                    onClick = { mainViewModel.deleteWorkedItem(workedHours) },
                    modifier = Modifier
                        .wrapContentWidth()
                        .defaultMinSize(minWidth = 2.dp),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")

                }
            }
        )
        HorizontalDivider()
    }
}