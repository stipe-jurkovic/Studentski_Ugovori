package com.example.studentskiugovori.model.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class Ugovori(
    val GODINA: List<Int?> = emptyList(),
    val UGOVOR: List<Int?> = emptyList(),
    val STATUSNAZIV: List<String?> = emptyList(),
    val PARTNERNAZIVWEB: List<String?> = emptyList(),
    val PARTNERNAZIV: List<String?> = emptyList(),
    val POSAONAZIV: List<String?> = emptyList(),
    val NETO: List<Double?> = emptyList(),
    val ISPLATANETO: List<Double?> = emptyList(),
    val VALUTAUNOS: List<String?> = emptyList(),
    val RADIOODWEB: List<String?> = emptyList(),
    val RADIODOWEB: List<String?> = emptyList(),
    val CIJENAWEB: List<Double?> = emptyList(),
    val STATUSWEB: List<Int?> = emptyList(),
)