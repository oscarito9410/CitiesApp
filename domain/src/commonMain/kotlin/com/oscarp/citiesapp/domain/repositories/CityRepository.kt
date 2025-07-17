package com.oscarp.citiesapp.domain.repositories

import com.oscarp.citiesapp.domain.models.CityDownload
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    /**
     * syncs city data (network → DB) in the background and
     * emits the total inserted so far.
     */
    fun syncCities(): Flow<CityDownload>

    suspend fun hasSyncCities(): Boolean
}
