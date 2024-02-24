package com.example.studentskiugovori.model.dataclasses

import java.util.Date

data class Ugovor(
    val GODINA: Int?,
    val UGOVOR: Int?,
    val STATUSNAZIV: String?,
    val PARTNERNAZIVWEB: String?,
    val PARTNERNAZIV: String?,
    val POSAONAZIV: String?,
    val NETO: Double?,
    val ISPLATANETO: Double?,
    val ISPLATA: String?,
    val VALUTAUNOS: String?,
    val RADIOODWEB: String?,
    val RADIODOWEB: String?,
    val CIJENAWEB: Double?,
    val JM: String?,
    val MJESTOOBAVLJANJA: String?,
    val STATUSWEB: Int?,
    val UPUCENWEB: Int?,
    val RAD: String?,
    val RACUN: Int?,
    val DATUMRACUNA: String?
)