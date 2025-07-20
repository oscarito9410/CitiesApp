package com.oscarp.citiesapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    hostState: SnackbarHostState,
    navController: NavHostController = rememberNavController(),
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = SyncCitiesDestination,
        modifier = Modifier.padding(contentPadding)
    ) {
        // Sync Cities screen
        composable<SyncCitiesDestination> {
            SyncScreen(navController)
        }

        // Cities screen
        composable<CitiesDestination> {
            CitiesScreen(hostState = hostState)
        }
    }
}
