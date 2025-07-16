package com.oscarp.citiesapp.synccities

/**
 * Represents the progress states emitted by SyncCitiesUseCase.
 */
data class SyncViewState(
    val isLoading: Boolean = false,
    val percentSync: Int = 0,
    val isCompleted: Boolean = false,
    val isError: Boolean = false,
    val isNoInternet: Boolean = false,
    val error: Throwable? = null
)
