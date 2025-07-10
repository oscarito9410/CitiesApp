package com.oscarp.citiesapp.domain.models

/**
 * Represents the progress states emitted by SyncCitiesUseCase.
 */
sealed class SyncProgress {
    object Started : SyncProgress()
    data class Inserting(val insertedCount: Int) : SyncProgress()
    object Completed : SyncProgress()
    data class Error(val throwable: Throwable) : SyncProgress()
}
