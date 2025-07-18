package com.oscarp.citiesapp.features.cities

data class CitiesViewState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val error: String? = null,
)
