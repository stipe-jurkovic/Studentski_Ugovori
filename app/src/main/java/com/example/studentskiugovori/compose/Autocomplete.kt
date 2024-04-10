package com.example.studentskiugovori.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import java.math.BigDecimal
import java.math.MathContext

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun autoComplete(
    suggestions: List<String> = listOf(
        "Lion",
        "Tiger",
        "Panda",
        "Gorilla",
        "Hippopotamus",
        "Rhinoceros",
        "Orangutan",
        "Polar Bear",
        "Grizzly Bear",
        "Sloth"
    ),
    selected: BigDecimal = BigDecimal(55.30)
): String? {

    var category by remember { mutableStateOf(selected.setScale(2).toPlainString()) }
    val heightTextFields by remember { mutableStateOf(55.dp) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .widthIn(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { expanded = false }
            )
    ) {

        Text(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            text = "Satnica",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Column(modifier = Modifier.widthIn(80.dp)) {

            Row(modifier = Modifier.wrapContentWidth()) {
                TextField(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(heightTextFields)
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp, 15.dp,0.dp, 0.dp )
                        ).clip(RoundedCornerShape(15.dp, 15.dp,0.dp, 0.dp ))
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    value = category,
                    onValueChange = {
                        category = it
                        expanded = true
                    },
                    suffix = { Text("€") },
                    placeholder = { Text("Unesi satnicu") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "arrow",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
            val animationSpecCustom = spring(dampingRatio = 0.8f, stiffness = 1000f, visibilityThreshold = 0.1f)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = animationSpecCustom),
                exit = fadeOut(animationSpec = animationSpecCustom)
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    shape = RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp),
                ) {

                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        if (category.isNotEmpty()) {
                            items(suggestions) {
                                ItemsCategory(title = it) { title ->
                                    category = title
                                    expanded = false
                                }
                            }
                        } else {
                            items(suggestions) {
                                ItemsCategory(title = it) { title ->
                                    category = title
                                    expanded = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return category

}

@Composable
fun ItemsCategory(
    title: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(title) }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}
