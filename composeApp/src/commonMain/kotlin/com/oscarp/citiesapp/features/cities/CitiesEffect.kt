package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage

sealed class CitiesEffect {
    object Idle : CitiesEffect()
    data class ShowSnackBar(val localizedMessage: LocalizedMessage) : CitiesEffect()
    data class NavigateToCityDetails(val city: City) : CitiesEffect()
    object RefreshCitiesPagination : CitiesEffect()
}
