package com.example.studentskiugovori.model.dataclasses

import java.time.LocalDate
import java.time.LocalTime

data class DayWorked(
    var date: LocalDate,
    var hours: Int,
    var timeStart: LocalTime,
    var timeEnd: LocalTime,
    var moneyEarned: Double,
    var completed: Boolean = true
)
