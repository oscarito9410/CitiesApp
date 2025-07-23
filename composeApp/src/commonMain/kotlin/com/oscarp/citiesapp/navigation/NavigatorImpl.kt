package com.oscarp.citiesapp.navigation

import androidx.navigation.NavHostController
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.mappers.toCityMapDetail

class NavigatorImpl(private val navController: NavHostController) : Navigator {
    override fun navigateToCityDetails(city: City) {
        navController.navigate(city.toCityMapDetail())
    }

    override fun popBackStack() {
        navController.popBackStack()
    }

    override fun navigateToSyncScreen() {
        navController.navigate(SyncCitiesDestination) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    override fun navigateToCitiesScreen() {
        navController.navigate(CitiesDestination) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
}
