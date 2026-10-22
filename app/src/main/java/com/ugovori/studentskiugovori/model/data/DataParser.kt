package com.ugovori.studentskiugovori.model.data

import com.ugovori.studentskiugovori.model.dataclasses.CardData
import com.ugovori.studentskiugovori.model.dataclasses.Ugovor
import com.ugovori.studentskiugovori.model.dataclasses.UgovoriData
import com.ugovori.studentskiugovori.model.dataclasses.WorkedHours
import com.ugovori.studentskiugovori.utils.Result.GenericResult
import com.ugovori.studentskiugovori.utils.Result.ParseResult
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


private val json = Json { ignoreUnknownKeys = true }

fun parseUgovore(data: String): ParseResult {
    try {
        val ugovoriData = json.decodeFromString<UgovoriData>(data)
        val lista = mutableListOf<Ugovor>()
        for (i in 0..<ugovoriData.total) {
            lista.add(
                Ugovor(
                    ugovoriData.rows.GODINA.getOrNull(i) ?: 0,
                    ugovoriData.rows.UGOVOR.getOrNull(i) ?: 0,
                    ugovoriData.rows.STATUSNAZIV.getOrNull(i) ?: "",
                    ugovoriData.rows.PARTNERNAZIVWEB.getOrNull(i) ?: "",
                    ugovoriData.rows.PARTNERNAZIV.getOrNull(i) ?: "",
                    ugovoriData.rows.POSAONAZIV.getOrNull(i) ?: "",
                    ugovoriData.rows.NETO.getOrNull(i) ?: 0.0,
                    ugovoriData.rows.ISPLATANETO.getOrNull(i) ?: 0.0,
                    ugovoriData.rows.VALUTAUNOS.getOrNull(i) ?: "",
                    ugovoriData.rows.RADIOODWEB.getOrNull(i)
                        ?.let { if (it.length >= 9) it.dropLast(9) else it } ?: "",
                    ugovoriData.rows.RADIODOWEB.getOrNull(i)
                        ?.let { if (it.length >= 9) it.dropLast(9) else it } ?: "",
                    ugovoriData.rows.CIJENAWEB.getOrNull(i) ?: 0.0,
                    ugovoriData.rows.STATUSWEB.getOrNull(i) ?: 0,
                )
            )
        }
        return ParseResult.Success(lista)
    } catch (e: Exception) {
        return ParseResult.Error(e.message ?: "Error parsing data")
    }
}

fun calculateEarningsAndGetNumbers(list: List<Ugovor>): CardData {
    var sum = 0.0
    var numOfPaid = 0
    var numOfIzdanih = 0
    for (ugovor in list) {
        if (ugovor.STATUSNAZIV?.contains("Ispl") == true && ugovor.NETO != null) {
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
    sum = sum.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    return CardData(sum, numOfPaid, numOfIzdanih)
}

fun calculateDayEarning(
    date: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime,
    hourly: BigDecimal,
    isOvertimeDay: Boolean = false
): GenericResult {
    val startHourMinutes = BigDecimal(startTime.hour * 60 + startTime.minute)
    val endHourMinutes = BigDecimal(endTime.hour * 60 + endTime.minute)
    val start = startHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)
    var end = endHourMinutes.divide(BigDecimal(60), 4, RoundingMode.HALF_UP)

    if (start == end) return GenericResult.Error("Start and end time are the same")
    if (end.stripTrailingZeros() == BigDecimal(0)) end = BigDecimal(24)
    if (end <= start) return GenericResult.Error("End time is before start time")

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
    } else if (start in otMorningEnd..<otNightStart) {
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

    return GenericResult.Success(
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
