package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.navigation.Navigator

class CitiesCoordinator(
    private val viewModel: CitiesViewModel,
    private val navigator: Navigator,
    private val analytics: AnalyticsService
) {
    fun onCitiesScreenLoaded() {
        analytics.logScreenView(
            SCREEN_CITIES_LIST,
            emptyMap()
        )
    }

    fun onCitySelected(city: City, isSinglePane: Boolean) {
        viewModel.processIntent(CitiesIntent.OnCitySelected(city))
        if (isSinglePane) {
            navigator.navigateToCityDetails(city)
            analytics.logScreenView(
                SCREEN_CITY_DETAIL,
                mapOf("cityId" to city.id)
            )
        } else {
            analytics.logEvent(
                EVENT_CITY_DETAILS_VIEWED_TWO_PANE,
                mapOf("cityId" to city.id)
            )
        }
    }

    fun onFavoriteToggled(city: City) {
        viewModel.processIntent(CitiesIntent.OnFavoriteToggled(city))
        analytics.logEvent(
            EVENT_CITY_FAVORITE_TOGGLED,
            mapOf("cityId" to city.id, "isFavorite" to !city.isFavorite)
        )
    }

    fun onSearchQueryChanged(query: String) {
        viewModel.processIntent(CitiesIntent.OnSearchQueryChanged(query))
    }

    fun onShowFavoritesFilterToggled(newState: Boolean) {
        viewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)
        analytics.logEvent(
            EVENT_SHOW_FAVORITES_FILTER_TOGGLED,
            mapOf("showOnlyFavorites" to newState)
        )
    }

    fun onInitialCitiesLoadError(errorMessage: String) {
        analytics.logEvent(
            EVENT_INITIAL_CITIES_LOAD_ERROR,
            mapOf("errorMessage" to errorMessage)
        )
    }

    fun onCitiesListDisplayedEmpty(searchQuery: String) {
        analytics.logEvent(
            EVENT_CITIES_LIST_EMPTY,
            mapOf("searchQuery" to searchQuery)
        )
    }

    companion object {
        const val SCREEN_CITY_DETAIL = "CityDetailScreen"
        const val SCREEN_CITIES_LIST = "CitiesListScreenLoaded"
        const val EVENT_CITY_DETAILS_VIEWED_TWO_PANE = "CityDetailsViewedInTwoPane"
        const val EVENT_CITY_FAVORITE_TOGGLED = "CityFavoriteToggled"
        const val EVENT_SHOW_FAVORITES_FILTER_TOGGLED = "ShowFavoritesFilterToggled"
        const val EVENT_INITIAL_CITIES_LOAD_ERROR = "InitialCitiesLoadError"
        const val EVENT_CITIES_LIST_EMPTY = "CitiesListDisplayedEmpty"
    }
}
