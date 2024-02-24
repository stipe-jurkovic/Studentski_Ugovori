package com.example.studomatisvu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.R
import com.example.studentskiugovori.model.dataclasses.Ugovor

@Composable
fun UgovorCompose(ugovor : Ugovor) {

    val shadow = Shadow(colorResource(id = R.color.black), Offset(3F, 3F), blurRadius = 10F)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()
            .shadow(elevation = 4.dp, shape = shape)
            .clip(shape)
    ) {
        Column(
            Modifier
                .background(color = colorResource(id = R.color.StudomatBlue))
                .zIndex(0F)
                .wrapContentHeight()
                .padding(4.dp, 2.dp, 4.dp, 2.dp)

        ) {
            Spacer(modifier = Modifier.height(4.dp))
             UgovorText(text = "Poslodavac: ", value = ugovor.PARTNERNAZIV ?:"")
        }
        Column(
            Modifier
                .background(color = colorResource(id = R.color.white))
                .zIndex(0F)
                //.padding(4.dp, 0.dp, 4.dp, 8.dp)
        ) {
            UgovorText("Status: ", ugovor.STATUSNAZIV ?: "")
            UgovorText("Partner: ", ugovor.PARTNERNAZIVWEB ?: "")
            UgovorText("Posao: ", ugovor.POSAONAZIV ?: "")
            UgovorText("Neto: ", ugovor.NETO.toString() ?: "")
            UgovorText("Isplata neto: ", ugovor.ISPLATANETO.toString() ?: "")
            UgovorText("Isplata: ", ugovor.ISPLATA ?: "")
            UgovorText("Valuta: ", ugovor.VALUTAUNOS ?: "")
            UgovorText("Radio od: ", ugovor.RADIOODWEB ?: "")
            UgovorText("Radio do: ", ugovor.RADIODOWEB ?: "")
            UgovorText("Cijena: ", ugovor.CIJENAWEB.toString() ?: "")
            UgovorText("Jedinica mjere: ", ugovor.JM ?: "")
            UgovorText("Mjesto obavljanja: ", ugovor.MJESTOOBAVLJANJA ?: "")
            UgovorText("Status web: ", ugovor.STATUSWEB.toString() ?: "")
            UgovorText("Upucen web: ", ugovor.UPUCENWEB.toString() ?: "")
            UgovorText("Rad: ", ugovor.RAD ?: "")
            UgovorText("Racun: ", ugovor.RACUN.toString() ?: "")
            UgovorText("Datum racuna: ", ugovor.DATUMRACUNA ?: "")
        }
    }
}

@Composable
fun UgovorText(text: String = "", value: String = "") {
    var modifier =
        Modifier
            .wrapContentWidth()
            .wrapContentHeight()
    var textColor = colorResource(id = R.color.black)

    if (text.contains("Poslodavac:")) {
        textColor = colorResource(id = R.color.white)
    }
    var modifierValue = modifier
    var textColorValue = textColor

    modifierValue = modifierValue.padding(8.dp, 8.dp, 16.dp, 8.dp)
    modifier = modifier.padding(16.dp, 8.dp, 8.dp, 8.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        //verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text, modifier = modifier,
            color = textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value, modifier = modifierValue,
            color = textColorValue,
            fontSize = 14.sp,
            textAlign = TextAlign.Right
        )
    }

}
