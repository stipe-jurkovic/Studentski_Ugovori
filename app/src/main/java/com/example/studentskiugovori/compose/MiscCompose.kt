package com.example.studentskiugovori.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.R

@Preview
@Composable
fun CircularIndicator() {
    Row(
        Modifier
            .fillMaxSize()
            .zIndex(1f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .clip(shape = CircleShape)
                .background(colorResource(id = R.color.md_theme_onPrimary))
                .padding(16.dp, 16.dp, 16.dp, 16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp),
                color = colorResource(id = R.color.md_theme_secondary),
                trackColor = colorResource(id = R.color.md_theme_secondary),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
        }
    }
}