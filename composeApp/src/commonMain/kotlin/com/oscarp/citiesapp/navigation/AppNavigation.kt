package com.oscarp.citiesapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.synccities.SyncScreen
import kotlinx.serialization.Serializable

// Define navigation destinations using serializable classes
@Serializable
object CitiesDestination

@Serializable
object SyncCitiesDestination

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SyncCitiesDestination
    ) {
        // Sync Cities screen
        composable<SyncCitiesDestination> {
            SyncScreen(navController)
        }

        // Cities screen
        composable<CitiesDestination> {
            CitiesScreen()
        }
    }
}
