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
    val VALUTAUNOS: List<String?>,
    val RADIOODWEB: List<String?>,
    val RADIODOWEB: List<String?>,
    val CIJENAWEB: List<Double?>,
    val STATUSWEB: List<Int?>
)