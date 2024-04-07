package com.example.studentskiugovori.model.dataclasses

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class WorkedHours(
    var id: UUID = UUID.randomUUID(),
    var date: LocalDate,
    var timeStart: LocalTime,
    var timeEnd: LocalTime,
    var moneyEarned: BigDecimal,
    var hours: BigDecimal = BigDecimal.ZERO,
    var hourlyPay: BigDecimal,
    var completed: Boolean = true,
)
