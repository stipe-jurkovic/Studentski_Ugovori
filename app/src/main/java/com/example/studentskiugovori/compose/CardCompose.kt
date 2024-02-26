package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

@Preview
@Composable
fun CardCompose() {
    Card(
        Modifier
            .aspectRatio((8.56 / 5.398).toFloat())
            .fillMaxSize()
    )
    {
        Box(
            modifier = Modifier
                .aspectRatio((8.56 / 5.398).toFloat())
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFBD2828),
                            Color(0xFF345894),
                            Color(0xFF22ADAD)
                        ),
                        tileMode = TileMode.Clamp,
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
        ) {
            Box(modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd))
            {
                Text(
                    text = "banana",
                    color = Color.White
                )
            }
            Box(modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart))
            {
                Text(
                    text = "banana",
                    color = Color.White
                )
            }


            /*val itemsList = (0..8).toList()
            val itemModifier = Modifier
                .border(1.dp, Color.Blue)
                .padding(16.dp)
                .fillMaxWidth()
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    Text(text = item.toString(), itemModifier)
                }
            }*/
            /*LazyColumn(Modifier.fillMaxSize(), content = {
                items(3) {
                    LazyRow(Modifier.fillMaxSize(), content = {
                        items(3) {
                            Box(modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()) {
                                Text(
                                    text = "Item $it",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize(),
                                    color = Color.White
                                )
                            }
                        }
                    })
                }
            })*/

            /*
                        Text(text = "banana", color = Color.White)
            */
        }

    }
}