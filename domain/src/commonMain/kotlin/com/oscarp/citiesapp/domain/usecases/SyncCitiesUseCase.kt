package com.oscarp.citiesapp.domain.usecases

import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.domain.models.SyncProgress
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

open class SyncCitiesUseCase(private val repository: CityRepository) {
    open operator fun invoke(): Flow<SyncProgress> =
        repository.syncCities()
            .map<Int, SyncProgress> { count ->
                Logger.i("inserting.. $count")
                SyncProgress.Inserting(count)
            }.onStart {
                Logger.setTag("SyncCitiesUseCase")
                Logger.i("started")
                emit(SyncProgress.Started)
            }
            .onCompletion {
                Logger.i("completed")
                emit(SyncProgress.Completed)
            }
            .catch {
                Logger.e("error", it)
                emit(SyncProgress.Error(it))
            }
}
