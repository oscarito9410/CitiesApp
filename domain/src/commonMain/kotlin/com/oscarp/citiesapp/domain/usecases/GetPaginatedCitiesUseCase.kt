package com.oscarp.citiesapp.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetPaginatedCitiesUseCase(
    private val factory: CitiesPagingSourceFactory,
    private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        searchQuery: String,
        onlyFavorites: Boolean
    ): Flow<PagingData<City>> {
        val pagingSource = factory.create(
            searchQuery,
            onlyFavorites
        )
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                pagingSource
            }
        ).flow.flowOn(ioDispatcher)
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
