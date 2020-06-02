package com.grayhatdevelopers.kontrol.models

data class Location(
    val address: String = "",
    val latitude: Double = 0.toDouble(),
    val longitude: Double = 0.toDouble()
)