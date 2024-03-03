package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentskiugovori.model.dataclasses.CardData

@Preview
@Composable
fun CardCompose(cardData: CardData = CardData()) {
    Card(
        Modifier
            .aspectRatio((8.56 / 5.398).toFloat())
            .fillMaxSize()
            .padding(8.dp)
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF3636),
                            Color(0xFF4D8FFF),
                            Color(0xFF30FFFF)
                        ),
                        tileMode = TileMode.Clamp,
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            )
            {
                Column() {
                    Text(
                        text = "Broj izdanih ugovora: " +cardData.numOfIzdanih,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                    Text(
                        text = "Broj isplaćenih ugovora: " +cardData.numOfPaid,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            )
            {
                Text(
                    text = "Trenutna zarada: " + cardData.sum + " €",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

        }

    }
}