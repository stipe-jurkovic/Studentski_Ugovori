package com.example.studentskiugovori.model.dataclasses

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class WorkedHours(
    var date: LocalDate,
    var timeStart: LocalTime,
    var timeEnd: LocalTime,
    var moneyEarned: BigDecimal,
    var hours: BigDecimal = BigDecimal.ZERO,
    var completed: Boolean = true,
)
