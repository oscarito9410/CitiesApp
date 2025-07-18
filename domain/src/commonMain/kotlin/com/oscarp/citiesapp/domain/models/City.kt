package com.oscarp.citiesapp.domain.models

import com.oscarp.citiesapp.domain.extenions.format

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

    val coordinates: String
        get() = "${latitude.format(4)}, ${longitude.format(4)}"
}
