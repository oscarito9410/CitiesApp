package com.oscarp.citiesapp.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

open class GetPaginatedCitiesUseCase(
    private val factory: CitiesPagingSourceFactory,
    private val repository: CityRepository,
    private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Retrieves a paginated stream of cities, enriched with their favorite status.
     *
     * This Flow is optimized for UI consumption using `cachedIn`.
     *
     * @param searchQuery The search query to filter cities.
     * @param onlyFavorites A boolean to show only favorite cities.
     * @param cachedIn A [CoroutineScope] (e.g., `viewModelScope`) to cache and share the [PagingData] stream.
     * This prevents multiple collections of the underlying PagingSource and ties
     * the data's lifecycle to the provided scope.
     * @return A [Flow] of [PagingData] containing [City] objects with updated favorite status.
     */
    open operator fun invoke(
        searchQuery: String,
        onlyFavorites: Boolean,
        cachedIn: CoroutineScope
    ): Flow<PagingData<City>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
        pagingSourceFactory = {
            factory.create(
                searchQuery,
                onlyFavorites
            )
        }
    ).flow
        .cachedIn(cachedIn)
        .combine(repository.getFavoriteCitiesIds()) { pagingData, favoriteCitiesIds ->
            pagingData.map { city ->
                val isCurrentlyFavoriteInDb = favoriteCitiesIds.contains(city.id)
                val updatedCity = city.copy(isFavorite = isCurrentlyFavoriteInDb)
                updatedCity
            }
        }
        .flowOn(ioDispatcher)

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
