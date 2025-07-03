package com.ugovori.studentskiugovori.navigation

import kotlinx.serialization.Serializable


@Serializable
data object FullList

@Serializable
data object Home

@Serializable
data object Calculation


data class TopLevelRoute<T : Any>(val nameId: Int, val route: T, val iconId: Int)