package com.example.studentskiugovori.model.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class Ugovori(
    val GODINA: List<Int?>,
    val UGOVOR: List<Int?>,
    val STATUSNAZIV: List<String?>,
    val PARTNERNAZIVWEB: List<String?>,
    val PARTNERNAZIV: List<String?>,
    val POSAONAZIV: List<String?>,
    val NETO: List<Double?>,
    val ISPLATANETO: List<Double?>,
    val ISPLATA: List<String?>,
    val VALUTAUNOS: List<String?>,
    val RADIOODWEB: List<String?>,
    val RADIODOWEB: List<String?>,
    val CIJENAWEB: List<Double?>,
    val JM: List<String?>,
    val MJESTOOBAVLJANJA: List<String?>,
    val STATUSWEB: List<Int?>,
    val UPUCENWEB: List<Int?>,
    val RAD: List<String?>,
    val RACUN: List<Int?>,
    val DATUMRACUNA: List<String?>
)