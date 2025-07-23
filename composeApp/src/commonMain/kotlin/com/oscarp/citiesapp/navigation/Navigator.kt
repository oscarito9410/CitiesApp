package com.oscarp.citiesapp.navigation

import com.oscarp.citiesapp.domain.models.City

interface Navigator {
    fun navigateToCityDetails(city: City)
    fun popBackStack()
    fun navigateToSyncScreen()
    fun navigateToCitiesScreen()
}
