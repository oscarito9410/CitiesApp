package com.oscarp.citiesapp.domain.factory

import app.cash.paging.PagingSource
import com.oscarp.citiesapp.domain.models.City

fun interface CitiesPagingSourceFactory {
    fun create(
        searchQuery: String,
        onlyFavorites: Boolean
    ): PagingSource<Int, City>
}
