package com.oscarp.citiesapp.data.factories

import androidx.paging.PagingSource
import com.oscarp.citiesapp.data.paging.CitiesPagingSourceImpl
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CitiesPagingSourceFactoryImplTest {

    private val repository = mock<CityRepository>()
    private val factory = CitiesPagingSourceFactoryImpl(repository)

    @Test
    fun `create returns CitiesPagingSourceImpl`() {
        val pagingSource = factory.create(
            searchQuery = "abc",
            onlyFavorites = true
        )

        assertIs<PagingSource<Int, City>>(pagingSource)
        assertIs<CitiesPagingSourceImpl>(pagingSource)
    }

    @Test
    fun `create returns distinct instances per call`() {
        val source1 = factory.create(
            searchQuery = "foo",
            onlyFavorites = false
        )
        val source2 = factory.create(
            searchQuery = "bar",
            onlyFavorites = true
        )

        assertTrue(source1 !== source2) // reference inequality
    }
}
