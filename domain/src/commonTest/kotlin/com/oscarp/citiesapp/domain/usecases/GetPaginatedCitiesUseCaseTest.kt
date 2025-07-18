package com.oscarp.citiesapp.domain.usecases

import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.testdoubles.FakeCitiesPagingSource
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GetPaginatedCitiesUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val factory = mock<CitiesPagingSourceFactory>()
    private val useCase = GetPaginatedCitiesUseCase(factory, dispatcher)

    @Test
    fun `invoke returns a Flow of PagingData`() = runTest(dispatcher) {
        // given
        val pagingSource = FakeCitiesPagingSource()
        every { factory.create(eq("mexico"), eq(false)) } returns pagingSource

        // when
        val result = useCase(searchQuery = "mexico", onlyFavorites = false).first()

        // then
        verify { factory.create(eq("mexico"), eq(false)) }
        assertIs<PagingData<City>>(result)
    }
}
