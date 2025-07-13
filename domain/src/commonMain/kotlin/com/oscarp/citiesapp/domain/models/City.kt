package com.oscarp.citiesapp.domain.models

data class City(
    val id: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    var isFavorite: Boolean = false,
) {
    val displayName: String
        get() = "$name, $country"
}
