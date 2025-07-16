package com.oscarp.citiesapp.features.synccities

/**
 * represents the progress states emitted.
 */
data class SyncViewState(
    val isLoading: Boolean = false,
    val percentSync: Int = 0,
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val isNoInternet: Boolean = false,
    val error: Throwable? = null
)
