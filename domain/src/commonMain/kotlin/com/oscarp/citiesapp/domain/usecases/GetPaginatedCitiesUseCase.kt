package com.oscarp.citiesapp.domain.usecases

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oscarp.citiesapp.domain.factory.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import kotlinx.coroutines.flow.Flow

class GetPaginatedCitiesUseCase(
    private val factory: CitiesPagingSourceFactory
) {
    operator fun invoke(
        searchQuery: String,
        onlyFavorites: Boolean
    ): Flow<PagingData<City>> {
        return app.cash.paging.Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                factory.create(
                    searchQuery,
                    onlyFavorites
                )
            }
        ).flow
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 50
    }
}
