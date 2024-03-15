package com.example.studentskiugovori.model.dataclasses

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class DayWorked(
    var date: LocalDate,
    var timeStart: LocalTime,
    var timeEnd: LocalTime,
    var moneyEarned: BigDecimal,
    var completed: Boolean = true,
    var hours: Int = 0,
)
