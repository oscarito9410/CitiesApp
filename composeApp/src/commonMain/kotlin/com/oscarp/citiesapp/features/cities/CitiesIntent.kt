package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.domain.models.City

sealed class CitiesIntent {
    data class OnSearchQueryChanged(val query: String) : CitiesIntent()
    data class OnFavoriteToggled(val city: City) : CitiesIntent()
    data class OnCitySelected(val city: City) : CitiesIntent()
    object OnShowFavoritesFilter : CitiesIntent()
}
