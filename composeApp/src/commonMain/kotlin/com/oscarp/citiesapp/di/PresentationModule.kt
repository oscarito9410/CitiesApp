package com.oscarp.citiesapp.di

import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.analytics.AnalyticsServiceImpl
import com.oscarp.citiesapp.domain.di.DispatchersQualifier
import com.oscarp.citiesapp.features.cities.CitiesCoordinator
import com.oscarp.citiesapp.features.cities.CitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.navigation.Navigator
import com.oscarp.citiesapp.navigation.NavigatorImpl
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun presentationModule(): Module = module {
    factory<Navigator> { (navController: NavHostController) -> NavigatorImpl(navController) }

    single<AnalyticsService> {
        AnalyticsServiceImpl(
            Logger.withTag("AnalyticsService")
        )
    }

    factory { (navController: NavHostController) ->
        CitiesCoordinator(
            navigator = get { parametersOf(navController) },
            analyticsService = get(),
            citiesViewModel = get()
        )
    }

    single<SyncCitiesViewModel> {
        SyncCitiesViewModel(
            get(),
            get(),
            get(
                named(
                    DispatchersQualifier.IO
                )
            )
        )
    }

    single<CitiesViewModel> {
        CitiesViewModel(
            get(),
            get(),
        )
    }
}
