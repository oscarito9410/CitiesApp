package com.oscarp.citiesapp.features.mapdetail

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.navigation.Navigator
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class MapDetailCoordinatorTest {

    lateinit var navigator: Navigator
    lateinit var analyticsService: AnalyticsService

    private lateinit var coordinator: MapDetailCoordinator

    @Before
    fun setUp() {
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        coordinator = MapDetailCoordinator(
            navigator = navigator,
            analytics = analyticsService
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `onScreenLoaded logs city detail screen`() {
        coordinator.onScreenLoaded()

        verify {
            analyticsService.logScreenView(MapDetailCoordinator.SCREEN_CITY_DETAIL, emptyMap())
        }
    }

    @Test
    fun `onBackClicked navigates back`() {
        coordinator.onBackClicked()

        verify {
            navigator.popBackStack()
        }
    }
}
