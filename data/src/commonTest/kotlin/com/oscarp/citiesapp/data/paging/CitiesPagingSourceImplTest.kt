package com.oscarp.citiesapp.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CitiesPagingSourceImplTest {

    private val repository: CityRepository = mock()
    private val searchQuery = "mexico"
    private val onlyFavorites = false

    private val pagingSource = CitiesPagingSourceImpl(
        repository,
        searchQuery,
        onlyFavorites
    )

    @Test
    fun `load returns page with data`() = runTest {
        // given
        val cities = listOf(
            City(
                id = 1,
                name = "CDMX",
                latitude = 19.43,
                longitude = -99.13,
                isFavorite = false,
                country = "MX"
            ),
            City(
                id = 2,
                name = "CDMX",
                latitude = 19.43,
                longitude = -99.13,
                isFavorite = false,
                country = "MX"
            )
        )
        everySuspend {
            repository.getPaginatedCities(
                any(),
                2,
                searchQuery,
                onlyFavorites
            )
        } returns cities

        val params = LoadParams.Refresh(
            key = 1,
            loadSize = 2,
            placeholdersEnabled = false
        )

        // When
        val result = pagingSource.load(params)

        // Then
        assertIs<Page<Int, City>>(result)
        assertEquals(cities, result.data)
        assertEquals(0, result.prevKey)
        result.nextKey?.let { assertTrue(it > 0) }
    }

    @Test
    fun `load returns page with empty data and no nextKey`() = runTest {
        everySuspend {
            repository.getPaginatedCities(1, 2, searchQuery, onlyFavorites)
        } returns emptyList()

        val params = LoadParams.Append(
            key = 1,
            loadSize = 2,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params)

        assertIs<LoadResult.Page<Int, City>>(result)
        assertEquals(emptyList(), result.data)
        assertEquals(0, result.prevKey)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `load returns error when repository throws`() = runTest {
        val exception = IOException("network down")
        everySuspend {
            repository.getPaginatedCities(
                any(),
                2,
                searchQuery,
                onlyFavorites
            )
        } throws exception

        val params = LoadParams.Refresh(
            key = 1,
            loadSize = 2,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params)

        assertIs<LoadResult.Error<Int, City>>(result)
        assertEquals(exception, result.throwable)
    }

    @Test
    fun `getRefreshKey returns anchorPosition`() {
        val state = PagingState(
            pages = emptyList<Page<Int, City>>(),
            anchorPosition = 10,
            config = PagingConfig(pageSize = 50),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(10, refreshKey)
    }
}
