package com.oscarp.citiesapp.domain.usecases

import app.cash.paging.PagingData
import app.cash.turbine.test
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.testdoubles.FakeCitiesPagingSource
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase.Companion.DEFAULT_PAGE_SIZE
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GetPaginatedCitiesUseCaseAndroidTest {

    private val dispatcher = StandardTestDispatcher()
    private val factory: CitiesPagingSourceFactory = mockk()
    private val useCase = GetPaginatedCitiesUseCase(factory, dispatcher)

    @Test
    fun `invoke returns a Flow of PagingData`() = runTest(dispatcher) {
        // given
        val pagingSource = FakeCitiesPagingSource()
        every { factory.create("mexico", false) } returns pagingSource

        useCase(
            searchQuery = "mexico",
            onlyFavorites = false
        ).test {
            verify {
                factory.create(
                    "mexico",
                    onlyFavorites = false
                )
            }
            val result = awaitItem()
            assertIs<PagingData<City>>(result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `assert pagination is 50`() = runTest {
        // given
        assertEquals(50, DEFAULT_PAGE_SIZE)
    }
}
