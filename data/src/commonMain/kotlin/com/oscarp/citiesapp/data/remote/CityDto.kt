package com.oscarp.citiesapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityDto(
    @SerialName("_id")
    val id: Long,
    val name: String,
    val country: String,
    val coord: CoordDto
)

@Serializable
data class CoordDto(
    val lon: Double,
    val lat: Double
)
