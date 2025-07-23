package com.oscarp.citiesapp.features.synccities

import com.oscarp.citiesapp.analytics.AnalyticsService
import com.oscarp.citiesapp.navigation.Navigator
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class SyncCitiesCoordinator(
    private val viewModel: SyncCitiesViewModel,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService
) {
    fun onScreenLoaded() {
        viewModel.processIntent(SyncIntent.VerifyLoadSync)
        analyticsService.logScreenView(
            SCREEN_SYNC_CITIES,
            emptyMap()
        )
    }

    fun onRetryClicked() {
        viewModel.processIntent(SyncIntent.StartSync)
        analyticsService.logEvent(
            EVENT_SYNC_RETRY,
            emptyMap()
        )
    }

    suspend fun onSyncCompleted() {
        analyticsService.logEvent(
            EVENT_SYNC_COMPLETED,
            emptyMap()
        )
        // smooth duration
        delay(1.seconds)
        navigator.navigateToCitiesScreen()
    }

    companion object {
        const val SCREEN_SYNC_CITIES = "SyncCitiesScreen"
        const val EVENT_SYNC_COMPLETED = "SyncCompleted"
        const val EVENT_SYNC_RETRY = "SyncRetry"
    }
}
