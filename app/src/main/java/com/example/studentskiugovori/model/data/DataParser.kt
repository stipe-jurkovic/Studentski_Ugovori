package com.example.studentskiugovori.model.data

import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.model.dataclasses.UgovoriData
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalTime


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

fun calculateEarningsAndGetNumbers(list: List<Ugovor>): CardData {
    var sum = 0.0
    var numOfPaid = 0
    var numOfIzdanih = 0
    for (ugovor in list) {
        if (ugovor.ISPLATA?.contains(".") == true && ugovor.STATUSNAZIV?.contains("Ispl") == true && ugovor.NETO != null) {
            if (ugovor.VALUTAUNOS == "EUR") {
                sum += ugovor.NETO
            } else if (ugovor.VALUTAUNOS == "KN") {
                sum += ugovor.NETO / 7.5345
            }
        }
        if (ugovor.STATUSNAZIV?.contains("Ispl") == true) {
            numOfPaid++
        }
        if (ugovor.STATUSNAZIV?.contains("Izdan") == true) {
            numOfIzdanih++
        }
    }
    sum = sum.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble()
    return CardData(sum, numOfPaid, numOfIzdanih)
}

fun calculateDayEarning(
    startTime: LocalTime,
    endTime: LocalTime,
    hourly: BigDecimal
): BigDecimal {
    val startHourMinutes = BigDecimal(startTime.hour * 60 + startTime.minute)
    val endHourMinutes = BigDecimal(endTime.hour * 60 + endTime.minute)
    val start = startHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)
    val end = endHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)

    var overtime = BigDecimal(0)
    var time = BigDecimal(0)
    val overtimeEnd = BigDecimal(22)
    val overtimeStart = BigDecimal(6)

    if (start in overtimeStart..overtimeEnd) {
        if (end in overtimeStart..overtimeEnd && end > start) {
            time += end - start
        } else if (end > overtimeEnd) {
            time += overtimeEnd - start
            overtime += end - overtimeEnd
        } else if (end < overtimeStart) {
            time += overtimeEnd - start
            overtime += end + BigDecimal(2)
        } else if (end >= overtimeStart) {
            time += overtimeEnd - start
            overtime += BigDecimal(8)
            time += end - overtimeStart
        }
    }
    if (start > overtimeEnd) {
        if (end >= overtimeEnd) {
            overtime += end - start
        } else if (end <= overtimeStart) {
            overtime += end + BigDecimal(2)
        } else if (end > overtimeStart) {
            overtime += BigDecimal(24) - start + overtimeStart
            time += end - overtimeStart
        }
    }
    if (start < overtimeStart) {
        if (end <= overtimeStart && start < end) {
            overtime += end - start
        }
        else if (end > overtimeStart) {
            overtime += overtimeStart - start
            time += end - overtimeStart
        }
    }

    return ((overtime.multiply(BigDecimal(1.5)) + time).multiply(hourly).setScale(2, RoundingMode.HALF_UP))
}









