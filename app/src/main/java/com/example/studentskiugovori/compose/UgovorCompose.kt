package com.example.studomatisvu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.studentskiugovori.R
import com.example.studentskiugovori.model.dataclasses.Ugovor

@Preview
@Composable
fun UgovorPreview() {
    /*UgovorComposeMini(
        ugovor = Ugovor(
            GODINA = 2021,
            UGOVOR = 19288,
            STATUSNAZIV = " Ispl - Plać",
            PARTNERNAZIVWEB = "STUDENTSKI CENTAR SPLIT",
            PARTNERNAZIV = "STUDENTSKI CENTAR SPLIT",
            POSAONAZIV = "ISPOMOĆ U KUHINJI",
            NETO = 1110.0,
            ISPLATANETO = 1110.0,
            ISPLATA = "05.11.2021",
            VALUTAUNOS = "KN",
            RADIOODWEB = "14.10.2021",
            RADIODOWEB = "31.10.2021",
            CIJENAWEB = 30.0,
            JM = "h",
            MJESTOOBAVLJANJA = "KAMPUS",
            STATUSWEB = 3,
            UPUCENWEB = 2,
            RAD = null,
            RACUN = 21860,
            DATUMRACUNA = "05.11.2021"
        )
    )*/
    var showFull : Boolean by remember { mutableStateOf (false) }
    Card(onClick = { showFull = !showFull }, modifier = Modifier.padding(0.dp).zIndex(3f)) {
        if (showFull) {
            UgovorComposeFull(
                ugovor = Ugovor(
                    GODINA = 2021,
                    UGOVOR = 19288,
                    STATUSNAZIV = " Ispl - Plać",
                    PARTNERNAZIVWEB = "STUDENTSKI CENTAR SPLIT",
                    PARTNERNAZIV = "STUDENTSKI CENTAR SPLIT",
                    POSAONAZIV = "ISPOMOĆ U KUHINJI",
                    NETO = 1110.0,
                    ISPLATANETO = 1110.0,
                    ISPLATA = "05.11.2021",
                    VALUTAUNOS = "KN",
                    RADIOODWEB = "14.10.2021",
                    RADIODOWEB = "31.10.2021",
                    CIJENAWEB = 30.0,
                    JM = "h",
                    MJESTOOBAVLJANJA = "KAMPUS",
                    STATUSWEB = 3,
                    UPUCENWEB = 2,
                    RAD = null,
                    RACUN = 21860,
                    DATUMRACUNA = "05.11.2021"
                )
            )
        } else {
            UgovorComposeMini(
                ugovor = Ugovor(
                    GODINA = 2021,
                    UGOVOR = 19288,
                    STATUSNAZIV = " Ispl - Plać",
                    PARTNERNAZIVWEB = "STUDENTSKI CENTAR SPLIT",
                    PARTNERNAZIV = "STUDENTSKI CENTAR SPLIT",
                    POSAONAZIV = "ISPOMOĆ U KUHINJI",
                    NETO = 1110.0,
                    ISPLATANETO = 1110.0,
                    ISPLATA = "05.11.2021",
                    VALUTAUNOS = "KN",
                    RADIOODWEB = "14.10.2021",
                    RADIODOWEB = "31.10.2021",
                    CIJENAWEB = 30.0,
                    JM = "h",
                    MJESTOOBAVLJANJA = "KAMPUS",
                    STATUSWEB = 3,
                    UPUCENWEB = 2,
                    RAD = null,
                    RACUN = 21860,
                    DATUMRACUNA = "05.11.2021"
                )
            )
        }
    }
}

@Composable
fun UgovorCompose(ugovor: Ugovor) {
    var showFull : Boolean by remember { mutableStateOf (false) }
    val shadow = Shadow(colorResource(id = R.color.black), Offset(3F, 3F), blurRadius = 10F)
    val shape = RoundedCornerShape(8.dp)

    val modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()
            .shadow(elevation = 4.dp, shape = shape)
            .clip(shape)
    Card(onClick = { showFull = !showFull }, modifier = modifier) {
        if (showFull) {
            UgovorComposeFull(ugovor)
        } else {
            UgovorComposeMini(ugovor)
        }
    }
}

@Composable
fun UgovorComposeFull(ugovor: Ugovor) {

    val shadow = Shadow(colorResource(id = R.color.black), Offset(3F, 3F), blurRadius = 10F)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .padding(0.dp)
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
            UgovorText(text = "Poslodavac: ", value = ugovor.PARTNERNAZIV ?: "")
        }
        Column(
            Modifier
                .background(color = colorResource(id = R.color.white))
                .zIndex(0F)
        ) {
            UgovorText("Status: ", ugovor.STATUSNAZIV ?: "")
            UgovorText("Posao: ", ugovor.POSAONAZIV ?: "")
            UgovorText(
                "Satnica: ",
                ugovor.CIJENAWEB.toString() + " " + ugovor.VALUTAUNOS + "/" + ugovor.JM
            )
            UgovorText(
                "Neto (Isplata neto): ",
                (ugovor.NETO.toString() ?: "") + " (" + (ugovor.ISPLATANETO.toString() ?: "") + ")" + " " + ugovor.VALUTAUNOS
            )
            UgovorText("Datum isplate: ", ugovor.ISPLATA ?: "")

            if ((ugovor.RADIOODWEB?.split(".")?.get(2) ?: "") == (ugovor.RADIODOWEB?.split(".")
                    ?.get(2) ?: "")
            ) {
                UgovorText(
                    "Radio: ",
                    (ugovor.RADIOODWEB?.dropLast(4) + " - " + ugovor.RADIODOWEB) ?: ""
                )
            } else {
                UgovorText("Radio: ", (ugovor.RADIOODWEB + " - " + ugovor.RADIODOWEB) ?: "")
            }

            UgovorText("Mjesto obavljanja: ", ugovor.MJESTOOBAVLJANJA ?: "")
            UgovorText("Račun: ", ugovor.RACUN.toString() ?: "")
            UgovorText("Datum racuna: ", ugovor.DATUMRACUNA ?: "")
        }
    }
}
@Composable
fun UgovorComposeMini(ugovor: Ugovor) {

    val shadow = Shadow(colorResource(id = R.color.black), Offset(3F, 3F), blurRadius = 10F)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .padding(0.dp)
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
            UgovorText(text = "Poslodavac: ", value = ugovor.PARTNERNAZIV ?: "")
        }
        Column(
            Modifier
                .background(color = colorResource(id = R.color.white))
                .zIndex(0F)
        ) {
            UgovorText("Status: ", ugovor.STATUSNAZIV ?: "")
            UgovorText(
                "Neto (Isplata neto): ",
                (ugovor.NETO.toString() ?: "") + " (" + (ugovor.ISPLATANETO.toString() ?: "") + ")" + " " + ugovor.VALUTAUNOS
            )
            UgovorText("Datum isplate: ", ugovor.ISPLATA ?: "")

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
    val textColorValue = textColor

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
