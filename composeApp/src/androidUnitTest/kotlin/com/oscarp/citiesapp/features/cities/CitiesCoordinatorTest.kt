package com.oscarp.citiesapp.features.cities

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.features.cities.CitiesCoordinator.Companion.EVENT_SEARCH_QUERY_CHANGED
import com.oscarp.citiesapp.navigation.Navigator
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class CitiesCoordinatorTest {

    private lateinit var viewModel: CitiesViewModel
    private lateinit var navigator: Navigator
    private lateinit var analytics: AnalyticsService
    private lateinit var coordinator: CitiesCoordinator

    private val fakeCity = City(
        id = 1L,
        name = "Test City",
        latitude = 10.0,
        longitude = 20.0,
        isFavorite = false,
        country = "MX"
    )

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)
        navigator = mockk(relaxed = true)
        analytics = mockk(relaxed = true)

        coordinator = CitiesCoordinator(
            viewModel = viewModel,
            navigator = navigator,
            analytics = analytics
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `onCitySelected - single pane - navigates and logs screen view`() {
        coordinator.onCitySelected(fakeCity, isSinglePane = true)

        verify {
            viewModel.processIntent(CitiesIntent.OnCitySelected(fakeCity))
            navigator.navigateToCityDetails(fakeCity)
            analytics.logScreenView(
                CitiesCoordinator.SCREEN_CITY_DETAIL,
                mapOf("cityId" to fakeCity.id)
            )
        }
    }

    @Test
    fun `onCitySelected - two pane - logs event only`() {
        coordinator.onCitySelected(fakeCity, isSinglePane = false)

        verify {
            viewModel.processIntent(CitiesIntent.OnCitySelected(fakeCity))
            analytics.logEvent(
                CitiesCoordinator.EVENT_CITY_DETAILS_VIEWED_TWO_PANE,
                mapOf("cityId" to fakeCity.id)
            )
        }

        verify(exactly = 0) { navigator.navigateToCityDetails(any()) }
    }

    @Test
    fun `onFavoriteToggled - logs event and sends intent`() {
        coordinator.onFavoriteToggled(fakeCity)

        verify {
            viewModel.processIntent(CitiesIntent.OnFavoriteToggled(fakeCity))
            analytics.logEvent(
                CitiesCoordinator.EVENT_CITY_FAVORITE_TOGGLED,
                mapOf("cityId" to fakeCity.id, "isFavorite" to true)
            )
        }
    }

    @Test
    fun `onSearchQueryChanged - sends event`() {
        val query = "Puebla"

        coordinator.onSearchQueryChanged(query)

        verify {
            viewModel.processIntent(CitiesIntent.OnSearchQueryChanged(query))
        }

        verify {
            analytics.logEvent(
                EVENT_SEARCH_QUERY_CHANGED,
                mapOf(
                    "searchQuery" to query
                )
            )
        }
        verify(exactly = 0) { analytics.logScreenView(any(), any()) }
    }

    @Test
    fun `onShowFavoritesFilterToggled - logs event and sends intent`() {
        coordinator.onShowFavoritesFilterToggled(true)

        verify {
            viewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)
            analytics.logEvent(
                CitiesCoordinator.EVENT_SHOW_FAVORITES_FILTER_TOGGLED,
                mapOf("showOnlyFavorites" to true)
            )
        }
    }

    @Test
    fun `onCitiesScreenLoaded - logs screen view`() {
        coordinator.onCitiesScreenLoaded()

        verify {
            analytics.logScreenView(
                CitiesCoordinator.SCREEN_CITIES_LIST,
                emptyMap()
            )
        }
    }

    @Test
    fun `onInitialCitiesLoadError - logs error event`() {
        val errorMessage = "Network timeout"

        coordinator.onInitialCitiesLoadError(errorMessage)

        verify {
            analytics.logEvent(
                CitiesCoordinator.EVENT_INITIAL_CITIES_LOAD_ERROR,
                mapOf("errorMessage" to errorMessage)
            )
        }
    }

    @Test
    fun `onCitiesListDisplayedEmpty - logs empty state event`() {
        val query = "Puebla"

        coordinator.onCitiesListDisplayedEmpty(query)

        verify {
            analytics.logEvent(
                CitiesCoordinator.EVENT_CITIES_LIST_EMPTY,
                mapOf("searchQuery" to query)
            )
        }
    }
}
