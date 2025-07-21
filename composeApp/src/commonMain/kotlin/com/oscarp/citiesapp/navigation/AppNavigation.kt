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
import androidx.navigation.toRoute
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.mapdetail.MapDetailScreen
import com.oscarp.citiesapp.features.synccities.SyncScreen
import com.oscarp.citiesapp.mappers.toCityMapDetail
import kotlinx.serialization.Serializable

@Serializable
object CitiesDestination

@Serializable
object SyncCitiesDestination

@Serializable
data class CityMapDetail(
    val id: Long,
    val name: String,
    val countryCode: String = "",
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)

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
        composable<SyncCitiesDestination> {
            SyncScreen(navController)
        }

        composable<CitiesDestination> {
            CitiesScreen(hostState = hostState) {
                navController.navigate(it.toCityMapDetail())
            }
        }

        composable<CityMapDetail> { backStackEntry ->
            val cityMapDetail = backStackEntry.toRoute<CityMapDetail>()
            MapDetailScreen(
                cityMapDetail,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
