package com.example.studentskiugovori.model.data

import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.model.dataclasses.UgovoriData
import kotlinx.serialization.json.Json


fun parseUgovore(data: String): List<Ugovor> {
    val ugovoriData = Json.decodeFromString<UgovoriData>(data)
    val lista = mutableListOf<Ugovor>()
    for (i in 0..<ugovoriData.total) {
        lista.add(
            Ugovor(
                ugovoriData.rows.GODINA[i],
                ugovoriData.rows.UGOVOR[i],
                ugovoriData.rows.STATUSNAZIV[i],
                ugovoriData.rows.PARTNERNAZIVWEB[i],
                ugovoriData.rows.PARTNERNAZIV[i],
                ugovoriData.rows.POSAONAZIV[i],
                ugovoriData.rows.NETO[i],
                ugovoriData.rows.ISPLATANETO[i],
                ugovoriData.rows.ISPLATA[i].toString().dropLast(9),
                ugovoriData.rows.VALUTAUNOS[i],
                ugovoriData.rows.RADIOODWEB[i].toString().dropLast(9),
                ugovoriData.rows.RADIODOWEB[i].toString().dropLast(9),
                ugovoriData.rows.CIJENAWEB[i],
                ugovoriData.rows.JM[i],
                ugovoriData.rows.MJESTOOBAVLJANJA[i],
                ugovoriData.rows.STATUSWEB[i],
                ugovoriData.rows.UPUCENWEB[i],
                ugovoriData.rows.RAD[i],
                ugovoriData.rows.RACUN[i],
                ugovoriData.rows.DATUMRACUNA[i].toString().dropLast(9)
            )
        )
    }
    return lista
}

fun calculateEarningsAndGetNumbers(list : List<Ugovor>): CardData {
    var sum = 0.0
    var numOfPaid = 0
    var numOfIzdanih = 0
    for(ugovor in list){
        if(ugovor.ISPLATA?.contains(".") == true && ugovor.STATUSNAZIV?.contains("Ispl") == true && ugovor.NETO != null){
            if(ugovor.VALUTAUNOS=="EUR")
            { sum += ugovor.NETO }
            else if(ugovor.VALUTAUNOS=="KN")
            { sum += ugovor.NETO / 7.5345 }
        }
        if (ugovor.STATUSNAZIV?.contains("Ispl") == true){
            numOfPaid++
        }
        if (ugovor.STATUSNAZIV?.contains("Izdan") == true ){
            numOfIzdanih++
        }
    }
    sum = sum.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble()
    return CardData(sum, numOfPaid, numOfIzdanih)
}
