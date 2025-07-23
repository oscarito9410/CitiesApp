package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage

sealed class CitiesEffect {
    object Idle : CitiesEffect()
    data class ShowSnackBar(val localizedMessage: LocalizedMessage) : CitiesEffect()
    object RefreshCitiesPagination : CitiesEffect()
}
