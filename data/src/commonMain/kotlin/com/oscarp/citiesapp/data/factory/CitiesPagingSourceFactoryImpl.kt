package com.oscarp.citiesapp.data.factory

import app.cash.paging.PagingSource
import com.oscarp.citiesapp.data.paging.CitiesPagingSourceImpl
import com.oscarp.citiesapp.domain.factory.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository

class CitiesPagingSourceFactoryImpl(
    private val repository: CityRepository
) : CitiesPagingSourceFactory {

    override fun create(
        searchQuery: String,
        onlyFavorites: Boolean
    ): PagingSource<Int, City> =
        CitiesPagingSourceImpl(
            repository,
            searchQuery,
            onlyFavorites
        )
}
