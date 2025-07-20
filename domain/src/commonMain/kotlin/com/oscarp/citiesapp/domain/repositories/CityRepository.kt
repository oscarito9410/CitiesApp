package com.oscarp.citiesapp.domain.repositories

import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.models.CityDownload
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    /**
     * syncs city data (network → DB) in the background and
     * emits the total inserted so far.
     */
    fun syncCities(): Flow<CityDownload>

    suspend fun hasSyncCities(): Boolean

    suspend fun getPaginatedCities(
        page: Int,
        loadSize: Int,
        searchQuery: String,
        onlyFavorites: Boolean
    ): List<City>

    suspend fun toggleFavorite(cityId: Long): Boolean

    fun getFavoriteCitiesIds(): Flow<Set<Long>>
}
