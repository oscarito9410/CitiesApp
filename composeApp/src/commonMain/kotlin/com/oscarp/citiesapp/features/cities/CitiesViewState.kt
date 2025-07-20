package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.domain.models.City

data class CitiesViewState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCity: City? = null,
    val showOnlyFavorites: Boolean = false,
    val error: String? = null,
)
