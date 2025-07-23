package com.oscarp.citiesapp.features.sync

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.features.synccities.SyncCitiesCoordinator
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncIntent
import com.oscarp.citiesapp.navigation.Navigator
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncCitiesCoordinatorTest {

    private lateinit var viewModel: SyncCitiesViewModel
    private lateinit var navigator: Navigator
    private lateinit var analyticsService: AnalyticsService
    private lateinit var coordinator: SyncCitiesCoordinator

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)

        coordinator = SyncCitiesCoordinator(
            viewModel = viewModel,
            navigator = navigator,
            analyticsService = analyticsService
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `onScreenLoaded dispatches VerifyLoadSync intent and logs screen view`() {
        // when
        coordinator.onScreenLoaded()

        // the
        verify { viewModel.processIntent(SyncIntent.VerifyLoadSync) }
        verify {
            analyticsService.logScreenView(
                SyncCitiesCoordinator.SCREEN_SYNC_CITIES,
                emptyMap()
            )
        }
    }

    @Test
    fun `onRetryClicked dispatches StartSync intent and logs retry event`() {
        // when
        coordinator.onRetryClicked()

        // then
        verify { viewModel.processIntent(SyncIntent.StartSync) }
        verify {
            analyticsService.logEvent(
                SyncCitiesCoordinator.EVENT_SYNC_RETRY,
                emptyMap()
            )
        }
    }

    @Test
    fun `onSyncCompleted logs completed event and navigates to cities screen`() = runTest {
        // when
        coordinator.onSyncCompleted()

        // then
        verifyOrder {
            analyticsService.logEvent(
                SyncCitiesCoordinator.EVENT_SYNC_COMPLETED,
                emptyMap()
            )
            navigator.navigateToCitiesScreen()
        }
    }
}
