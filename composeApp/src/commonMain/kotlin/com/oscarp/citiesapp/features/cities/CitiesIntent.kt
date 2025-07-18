package com.oscarp.citiesapp.features.cities

sealed class CitiesIntent {
    data class Search(val query: String) : CitiesIntent()
    object ToggleFavorite : CitiesIntent()
}
