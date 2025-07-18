package com.oscarp.citiesapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.io.IOException

class CitiesPagingSourceImpl(
    private val repository: CityRepository,
    private val searchQuery: String,
    private val onlyFavorites: Boolean
) : PagingSource<Int, City>() {

    override fun getRefreshKey(state: PagingState<Int, City>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
        val page = params.key ?: 0
        return try {
            val cities =
                repository.getPaginatedCities(
                    page,
                    params.loadSize,
                    searchQuery,
                    onlyFavorites
                )
            LoadResult.Page(
                data = cities,
                prevKey = if (page == 0) {
                    null
                } else {
                    page - 1
                },
                nextKey = if (cities.isEmpty()) {
                    null
                } else {
                    page + 1
                }
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }
}
