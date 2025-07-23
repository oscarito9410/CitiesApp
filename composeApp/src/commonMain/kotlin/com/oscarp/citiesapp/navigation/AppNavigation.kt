package com.oscarp.citiesapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oscarp.citiesapp.features.cities.CitiesCoordinator
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.cities.CitiesViewModel
import com.oscarp.citiesapp.features.mapdetail.MapDetailCoordinator
import com.oscarp.citiesapp.features.mapdetail.MapDetailScreen
import com.oscarp.citiesapp.features.synccities.SyncCitiesCoordinator
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

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
            val viewModel: SyncCitiesViewModel = koinInject()
            val coordinator = rememberSyncCitiesCoordinator(navController)

            SyncScreen(
                viewModel = viewModel,
                coordinator = coordinator
            )
        }

        composable<CitiesDestination> {
            val citiesViewModel: CitiesViewModel = koinInject()
            val citiesCoordinator: CitiesCoordinator = rememberCitiesCoordinator(navController)
            CitiesScreen(
                viewModel = citiesViewModel,
                coordinator = citiesCoordinator,
                hostState = hostState
            )
        }

        composable<CityMapDetail> { backStackEntry ->
            val cityMapDetail = backStackEntry.toRoute<CityMapDetail>()
            val coordinator = rememberMapDetailCoordinator(navController)

            MapDetailScreen(
                cityMapDetail = cityMapDetail,
                coordinator = coordinator
            )
        }
    }
}

@Composable
fun rememberCitiesCoordinator(
    navController: NavHostController
): CitiesCoordinator {
    val injectedCoordinator = koinInject<CitiesCoordinator>(parameters = {
        parametersOf(navController)
    })
    return remember(navController) {
        injectedCoordinator
    }
}

@Composable
fun rememberSyncCitiesCoordinator(
    navController: NavHostController
): SyncCitiesCoordinator {
    val injectedCoordinator = koinInject<SyncCitiesCoordinator>(parameters = {
        parametersOf(navController)
    })
    return remember(navController) { injectedCoordinator }
}

@Composable
fun rememberMapDetailCoordinator(
    navController: NavHostController
): MapDetailCoordinator {
    val injectedCoordinator = koinInject<MapDetailCoordinator>(parameters = {
        parametersOf(navController)
    })
    return remember(navController) { injectedCoordinator }
}
