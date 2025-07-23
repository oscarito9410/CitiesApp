package com.oscarp.citiesapp.features.mapdetail

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.navigation.Navigator

class MapDetailCoordinator(
    private val navigator: Navigator,
    private val analytics: AnalyticsService
) {
    fun onScreenLoaded() {
        analytics.logScreenView(SCREEN_CITY_DETAIL, emptyMap())
    }

    fun onBackClicked() {
        navigator.popBackStack()
    }

    companion object {
        const val SCREEN_CITY_DETAIL = "CityDetailScreen"
    }
}
