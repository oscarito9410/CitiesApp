package com.oscarp.citiesapp.domain.usecases

import com.oscarp.citiesapp.domain.models.CityDownload
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.flow.Flow

open class SyncCitiesUseCase(private val repository: CityRepository) {
    open operator fun invoke(): Flow<CityDownload> =
        repository.syncCities()
}
