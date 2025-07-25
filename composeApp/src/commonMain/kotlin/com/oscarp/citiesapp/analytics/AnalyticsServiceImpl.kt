package com.oscarp.citiesapp.analytics

import co.touchlab.kermit.Logger

/**
 * TODO change this to a real implementation using firebase analytics
 */
class AnalyticsServiceImpl(private val logger: Logger) : AnalyticsService {

    override fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        logger.i("Logging event: $eventName with params: $params")
    }

    override fun logScreenView(
        screenName: String,
        params: Map<String, Any>
    ) {
        logger.i("Logging screen view: $screenName with params: $params")
    }
}
