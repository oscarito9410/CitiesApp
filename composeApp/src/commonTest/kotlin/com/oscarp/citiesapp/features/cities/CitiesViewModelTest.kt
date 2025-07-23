@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.features.cities

import app.cash.paging.PagingData
import app.cash.turbine.test
import com.oscarp.citiesapp.domain.exceptions.CityNotFoundException
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase
import com.oscarp.citiesapp.domain.usecases.ToggleFavoriteUseCase
import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CitiesViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getPaginatedCitiesUseCase: GetPaginatedCitiesUseCase = mock()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private lateinit var viewModel: CitiesViewModel

    private val fakeCity = City(
        id = 1,
        name = "Mexico City",
        country = "Mexico",
        isFavorite = false,
        latitude = 0.0,
        longitude = 0.0
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every {
            getPaginatedCitiesUseCase.invoke(
                any(),
                any(),
                any()
            )
        } returns flowOf(PagingData.empty())
        viewModel = CitiesViewModel(
            getPaginatedCitiesUseCase,
            toggleFavoriteUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processIntent Search updates query in state`() = runTest(dispatcher) {
        // when
        viewModel.processIntent(CitiesIntent.OnSearchQueryChanged("cdmx"))
        advanceTimeBy(350)

        // then
        assertEquals(
            "cdmx",
            viewModel.state.value.searchQuery
        )
    }

    @Test
    fun `processIntent OnCitySelected sets city in state`() = runTest(dispatcher) {
        // initial state
        assertNull(viewModel.state.value.selectedCity)

        // when
        viewModel.processIntent(
            CitiesIntent.OnCitySelected(
                fakeCity,
            )
        )

        // then
        assertEquals(
            fakeCity,
            viewModel.state.value.selectedCity
        )
    }

    @Test
    fun `processIntent ToggleFavorite inverts showOnlyFavorites in state`() = runTest(dispatcher) {
        // initial state
        assertFalse(viewModel.state.value.showOnlyFavorites)

        // when
        viewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)

        // then
        assertTrue(viewModel.state.value.showOnlyFavorites)
    }

    @Test
    fun `paginatedCities emits PagingData when state changes`() = runTest(dispatcher) {
        // when
        viewModel.processIntent(CitiesIntent.OnSearchQueryChanged("oaxaca"))
        advanceTimeBy(350)

        // then
        val result = viewModel.paginatedCities.value
        assertIs<PagingData<City>>(result)
    }

    @Test
    fun `toggleFavorite success emits RefreshCitiesPagination when removing from favorites with filter on`() =
        runTest(dispatcher) {
            // given
            val favoriteCity = fakeCity.copy(isFavorite = true)
            everySuspend { toggleFavoriteUseCase(favoriteCity.id) } returns true
            viewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)

            // when
            viewModel.processIntent(CitiesIntent.OnFavoriteToggled(favoriteCity))

            // then
            viewModel.uiEffect.test {
                val effect = awaitItem()
                assertIs<CitiesEffect.RefreshCitiesPagination>(
                    effect,
                    "the effect should be RefreshCitiesPagination"
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggleFavorite success does NOT emit effect when adding to favorites`() =
        runTest(dispatcher) {
            // given
            val nonFavoriteCity = fakeCity.copy(isFavorite = false)
            everySuspend { toggleFavoriteUseCase(nonFavoriteCity.id) } returns true

            // when & then
            viewModel.uiEffect.test {
                viewModel.processIntent(CitiesIntent.OnFavoriteToggled(nonFavoriteCity))
                advanceUntilIdle()

                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggleFavorite failure emits FailedToUpdateFavoriteStatus snackbar`() =
        runTest(dispatcher) {
            // given
            everySuspend { toggleFavoriteUseCase(fakeCity.id) } returns false

            // when & then
            viewModel.uiEffect.test {
                viewModel.processIntent(CitiesIntent.OnFavoriteToggled(fakeCity))
                advanceUntilIdle()

                val expected =
                    CitiesEffect.ShowSnackBar(LocalizedMessage.FailedToUpdateFavoriteStatus)
                assertEquals(expected, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `toggleFavorite throws CityNotFoundException emits CityNotFound snackbar`() =
        runTest(dispatcher) {
            // given
            everySuspend { toggleFavoriteUseCase(fakeCity.id) } throws CityNotFoundException("No city found")

            // when & then
            viewModel.uiEffect.test {
                viewModel.processIntent(CitiesIntent.OnFavoriteToggled(fakeCity))
                advanceUntilIdle()

                val expected = CitiesEffect.ShowSnackBar(LocalizedMessage.CityNotFound)
                assertEquals(expected, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
}
