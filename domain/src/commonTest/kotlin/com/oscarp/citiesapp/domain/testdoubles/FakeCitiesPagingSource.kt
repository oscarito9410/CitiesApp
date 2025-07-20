package com.oscarp.citiesapp.domain.testdoubles

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import com.oscarp.citiesapp.domain.models.City

class FakeCitiesPagingSource : PagingSource<Int, City>() {
    override fun getRefreshKey(state: PagingState<Int, City>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
        return LoadResult.Page(
            data = listOf(
                City(
                    id = 1,
                    name = "Puebla",
                    latitude = 0.0,
                    longitude = 0.0,
                    isFavorite = false,
                    country = "MX"
                ),
                City(
                    id = 2,
                    name = "Puebla",
                    latitude = 0.0,
                    longitude = 0.0,
                    isFavorite = false,
                    country = "MX"
                ),
                City(
                    id = 3,
                    name = "Puebla",
                    latitude = 0.0,
                    longitude = 0.0,
                    isFavorite = false,
                    country = "MX"
                )
            ),
            prevKey = null,
            nextKey = 1
        )
    }
}
