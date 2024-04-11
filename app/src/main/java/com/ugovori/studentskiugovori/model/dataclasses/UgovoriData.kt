package com.ugovori.studentskiugovori.model.dataclasses

import kotlinx.serialization.Serializable

@Serializable
data class UgovoriData(
    val total: Int,
    val page : Int,
    val pageCnt: Int,
    val rows: Ugovori,
    val userdata: String?
)
