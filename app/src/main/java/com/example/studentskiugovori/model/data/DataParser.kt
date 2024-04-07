package com.example.studentskiugovori.model.data

import com.example.studentskiugovori.model.Result.Result
import com.example.studentskiugovori.model.dataclasses.CardData
import com.example.studentskiugovori.model.dataclasses.Ugovor
import com.example.studentskiugovori.model.dataclasses.UgovoriData
import com.example.studentskiugovori.model.dataclasses.WorkedHours
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


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
    date: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime,
    hourly: BigDecimal,
    isOvertimeDay: Boolean = false
): Result<WorkedHours> {
    val startHourMinutes = BigDecimal(startTime.hour * 60 + startTime.minute)
    val endHourMinutes = BigDecimal(endTime.hour * 60 + endTime.minute)
    val start = startHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)
    var end = endHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)

    if (start == end) return Result.Error("Start and end time are the same")
    if (end.stripTrailingZeros() == BigDecimal(0)) end = BigDecimal(24)
    if (end <= start) return Result.Error("End time is before start time")

    var overtime = BigDecimal(0)
    var time = BigDecimal(0)
    val otNightStart = BigDecimal(22)
    val otMorningEnd = BigDecimal(6)

    if (start < otMorningEnd) {
        if (end <= otMorningEnd) {
            overtime += end - start
        } else if (end > otMorningEnd) {
            overtime += otMorningEnd - start
            time += end - otMorningEnd
        } else if (end >= otNightStart) {
            overtime += otMorningEnd - start
            time += BigDecimal(16)
            overtime += end - otNightStart
        }
    } else if (start >= otMorningEnd && start < otNightStart) {
        if (end <= otNightStart) {
            time += end - start
        } else if (end > otNightStart) {
            time = otNightStart - start
            overtime += end - otNightStart
        }
    } else if (start >= otNightStart) {
        overtime += end - start
    }

    val result = if (isOvertimeDay) {
        (overtime + time).multiply(BigDecimal(1.5))
            .multiply(hourly)
            .setScale(2, RoundingMode.HALF_UP)
    } else {
        (overtime.multiply(BigDecimal(1.5)) + time)
            .multiply(hourly)
            .setScale(2, RoundingMode.HALF_UP)
    }

    return Result.Success(
        WorkedHours(
            UUID.randomUUID(),
            date,
            startTime,
            endTime,
            result,
            (overtime + time).stripTrailingZeros(),
            hourly
        )
    )
}
