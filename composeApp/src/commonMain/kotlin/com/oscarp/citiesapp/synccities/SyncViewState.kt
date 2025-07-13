package com.oscarp.citiesapp.synccities

/**
 * Represents the progress states emitted by SyncCitiesUseCase.
 */
sealed class SyncViewState {
    object Idle : SyncViewState()
    object Loading : SyncViewState()
    data class Inserting(val count: Int) : SyncViewState()
    object Completed : SyncViewState()
    data class Error(val error: Throwable) : SyncViewState()
}
