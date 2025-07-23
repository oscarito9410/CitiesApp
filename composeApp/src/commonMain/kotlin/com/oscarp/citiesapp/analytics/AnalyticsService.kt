package com.oscarp.citiesapp.analytics

interface AnalyticsService {
    fun logEvent(
        eventName: String,
        params: Map<String, Any>
    )

    fun logScreenView(
        screenName: String,
        params: Map<String, Any>
    )
}
