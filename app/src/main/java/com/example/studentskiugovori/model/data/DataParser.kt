package com.example.studentskiugovori.model.data

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