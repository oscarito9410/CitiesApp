package com.oscarp.citiesapp.domain.usecases

import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

open class GetPaginatedCitiesUseCase(
    private val factory: CitiesPagingSourceFactory,
    private val ioDispatcher: CoroutineDispatcher
) {
    open operator fun invoke(
        searchQuery: String,
        onlyFavorites: Boolean
    ): Flow<PagingData<City>> {
        return app.cash.paging.Pager(
            config = app.cash.paging.PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                factory.create(
                    searchQuery,
                    onlyFavorites
                )
            }
        ).flow.flowOn(ioDispatcher)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 50
    }
}
