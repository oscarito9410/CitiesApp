package com.oscarp.citiesapp.domain.usecases

import com.oscarp.citiesapp.domain.repositories.CityRepository

open class HasSyncCitiesUseCase(private val repository: CityRepository) {
    suspend operator fun invoke(): Boolean =
        repository.hasSyncCities()
}
