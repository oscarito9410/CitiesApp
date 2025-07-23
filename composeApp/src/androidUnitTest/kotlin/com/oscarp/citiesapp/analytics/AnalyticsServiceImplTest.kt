package com.oscarp.citiesapp.analytics

import co.touchlab.kermit.Logger
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class AnalyticsServiceImplTest {

    private lateinit var logger: Logger
    private lateinit var analyticsService: AnalyticsServiceImpl

    @Before
    fun setUp() {
        logger = mockk(relaxed = true)
        analyticsService = AnalyticsServiceImpl(logger)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `logEvent logs CityFavoriteToggled correctly`() {
        val cityId = 42L
        val isFavorite = true
        val params = mapOf("cityId" to cityId, "isFavorite" to isFavorite)

        analyticsService.logEvent("CityFavoriteToggled", params)

        verify {
            logger.i("Logging event: CityFavoriteToggled with params: $params")
        }
    }

    @Test
    fun `logEvent logs ShowFavoritesFilterToggled correctly`() {
        val params = mapOf("showOnlyFavorites" to true)

        analyticsService.logEvent("ShowFavoritesFilterToggled", params)

        verify {
            logger.i("Logging event: ShowFavoritesFilterToggled with params: $params")
        }
    }

    @Test
    fun `logScreenView logs CitiesListScreen`() {
        analyticsService.logScreenView("CitiesListScreen", emptyMap())

        verify {
            logger.i("Logging screen view: CitiesListScreen with params: {}")
        }
    }

    @Test
    fun `logScreenView logs CityDetailScreen with parameters`() {
        val params = mapOf("cityId" to 123L, "source" to "CitiesList")

        analyticsService.logScreenView("CityDetailScreen", params)

        verify {
            logger.i("Logging screen view: CityDetailScreen with params: $params")
        }
    }

    @Test
    fun `logEvent logs InitialCitiesLoadError with errorMessage`() {
        val params = mapOf("errorMessage" to "Timeout")

        analyticsService.logEvent("InitialCitiesLoadError", params)

        verify {
            logger.i("Logging event: InitialCitiesLoadError with params: $params")
        }
    }

    @Test
    fun `logEvent logs CitiesListDisplayedEmpty with searchQuery`() {
        val params = mapOf("searchQuery" to "Puebla")

        analyticsService.logEvent("CitiesListDisplayedEmpty", params)

        verify {
            logger.i("Logging event: CitiesListDisplayedEmpty with params: $params")
        }
    }
}
