package com.example.studentskiugovori.model.dataclasses

import java.util.Date

data class Ugovor(
    val GODINA: Int? = 0,
    val UGOVOR: Int? = 0,
    val STATUSNAZIV: String? = "",
    val PARTNERNAZIVWEB: String? = "",
    val PARTNERNAZIV: String? = "",
    val POSAONAZIV: String? = "",
    val NETO: Double? = 0.0,
    val ISPLATANETO: Double? = 0.0,
    val VALUTAUNOS: String? = "",
    val RADIOODWEB: String? = "",
    val RADIODOWEB: String? = "",
    val CIJENAWEB: Double? = 0.0,
    val STATUSWEB: Int? = 0
)