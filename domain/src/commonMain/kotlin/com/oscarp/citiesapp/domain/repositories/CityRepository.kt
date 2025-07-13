package com.oscarp.citiesapp.domain.repositories

import kotlinx.coroutines.flow.Flow

interface CityRepository {
    /**
     * Syncs city data (network → DB) in the background and
     * emits the total inserted so far.
     */
    fun syncCities(): Flow<Int>
}
