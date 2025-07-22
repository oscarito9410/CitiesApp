package com.oscarp.citiesapp.domain.usecases

import app.cash.paging.PagingData
import app.cash.paging.map
import app.cash.turbine.test
import com.oscarp.citiesapp.domain.factories.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.repositories.CityRepository
import com.oscarp.citiesapp.domain.testdoubles.FakeCitiesPagingSource
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase.Companion.DEFAULT_PAGE_SIZE
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetPaginatedCitiesUseCaseAndroidTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val factory: CitiesPagingSourceFactory = mockk()
    private val repository: CityRepository = mockk()

    private val useCase = GetPaginatedCitiesUseCase(
        factory,
        repository,
        testDispatcher
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns a Flow of PagingData when favorites are mapped`() =
        runTest(testDispatcher) {
            // given
            val pagingSource = FakeCitiesPagingSource(
                cities = listOf(
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
                )
            )
            every { repository.getFavoriteCitiesIds() } returns flowOf(setOf(1L, 2L, 3L, 4L))
            every {
                factory.create(
                    "mexico",
                    onlyFavorites = false
                )
            } returns pagingSource

            useCase(
                searchQuery = "mexico",
                onlyFavorites = false,
                cachedIn = testScope
            ).test {
                val result = awaitItem()
                assertIs<PagingData<City>>(result)
                result.map { city ->
                    assertTrue("must be favorite") {
                        city.isFavorite
                    }
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke returns a Flow of PagingData when favorites are not mapped`() =
        runTest(testDispatcher) {
            // given
            val pagingSource = FakeCitiesPagingSource()
            every { repository.getFavoriteCitiesIds() } returns flowOf(setOf(10L, 20L, 30L, 40L))
            every {
                factory.create(
                    "mexico",
                    onlyFavorites = false
                )
            } returns pagingSource

            useCase(
                searchQuery = "mexico",
                onlyFavorites = false,
                cachedIn = testScope
            ).test {
                val result = awaitItem()
                assertIs<PagingData<City>>(result)
                result.map { city ->
                    assertFalse("must not be favorite") {
                        city.isFavorite
                    }
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `assert pagination is 50`() = runTest(testDispatcher) {
        // given
        assertEquals(50, DEFAULT_PAGE_SIZE)
    }
}
