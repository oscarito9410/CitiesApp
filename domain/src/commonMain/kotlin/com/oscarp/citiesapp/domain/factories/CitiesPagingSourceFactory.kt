package com.oscarp.citiesapp.domain.factories

import app.cash.paging.PagingSource
import com.oscarp.citiesapp.domain.models.City

fun interface CitiesPagingSourceFactory {
    fun create(
        searchQuery: String,
        onlyFavorites: Boolean
    ): PagingSource<Int, City>
}
