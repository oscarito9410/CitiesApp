package com.oscarp.citiesapp.domain.testdoubles

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oscarp.citiesapp.domain.models.City

class FakeCitiesPagingSource : PagingSource<Int, City>() {
    override fun getRefreshKey(state: PagingState<Int, City>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
        return LoadResult.Page(
            data = listOf(
                City(
                    id = 1,
                    name = "Puebla",
                    latitude = 0.0,
                    longitude = 0.0,
                    isFavorite = true,
                    country = "MX"
                )
            ),
            prevKey = null,
            nextKey = null
        )
    }
}
