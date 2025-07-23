package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.navigation.Navigator

class CitiesCoordinator(
    private val citiesViewModel: CitiesViewModel,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService
) {

    fun onCitySelected(city: City, isSinglePane: Boolean) {
        citiesViewModel.processIntent(CitiesIntent.OnCitySelected(city))
        if (isSinglePane) {
            navigator.navigateToCityDetails(city)
            analyticsService.logScreenView(
                SCREEN_CITY_DETAIL,
                mapOf("cityId" to city.id)
            )
        } else {
            analyticsService.logEvent(
                EVENT_CITY_DETAILS_VIEWED_TWO_PANE,
                mapOf("cityId" to city.id)
            )
        }
    }

    fun onFavoriteToggled(city: City) {
        citiesViewModel.processIntent(CitiesIntent.OnFavoriteToggled(city))
        analyticsService.logEvent(
            EVENT_CITY_FAVORITE_TOGGLED,
            mapOf("cityId" to city.id, "isFavorite" to !city.isFavorite)
        )
    }

    fun onSearchQueryChanged(query: String) {
        citiesViewModel.processIntent(CitiesIntent.OnSearchQueryChanged(query))
    }

    fun onShowFavoritesFilterToggled(newState: Boolean) {
        citiesViewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)
        analyticsService.logEvent(
            EVENT_SHOW_FAVORITES_FILTER_TOGGLED,
            mapOf("showOnlyFavorites" to newState)
        )
    }

    fun onCitiesScreenEntered() {
        analyticsService.logScreenView(
            SCREEN_CITIES_LIST,
            emptyMap()
        )
    }

    fun onInitialCitiesLoadError(errorMessage: String) {
        analyticsService.logEvent(
            EVENT_INITIAL_CITIES_LOAD_ERROR,
            mapOf("errorMessage" to errorMessage)
        )
    }

    fun onCitiesListDisplayedEmpty(searchQuery: String) {
        analyticsService.logEvent(
            EVENT_CITIES_LIST_EMPTY,
            mapOf("searchQuery" to searchQuery)
        )
    }

    companion object {
        const val SCREEN_CITY_DETAIL = "CityDetailScreen"
        const val SCREEN_CITIES_LIST = "CitiesListScreen"
        const val EVENT_CITY_DETAILS_VIEWED_TWO_PANE = "CityDetailsViewedInTwoPane"
        const val EVENT_CITY_FAVORITE_TOGGLED = "CityFavoriteToggled"
        const val EVENT_SHOW_FAVORITES_FILTER_TOGGLED = "ShowFavoritesFilterToggled"
        const val EVENT_INITIAL_CITIES_LOAD_ERROR = "InitialCitiesLoadError"
        const val EVENT_CITIES_LIST_EMPTY = "CitiesListDisplayedEmpty"
    }
}
