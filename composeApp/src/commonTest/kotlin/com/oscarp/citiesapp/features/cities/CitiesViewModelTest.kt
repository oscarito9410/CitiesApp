@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.features.cities

import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CitiesViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val useCase: GetPaginatedCitiesUseCase = mock()
    private lateinit var viewModel: CitiesViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { useCase.invoke(any(), any()) } returns flowOf(PagingData.empty())
        viewModel = CitiesViewModel(useCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processIntent Search updates query in state`() = runTest(dispatcher) {
        // when
        viewModel.processIntent(CitiesIntent.Search("cdmx"))
        advanceTimeBy(350)

        // then
        assertEquals("cdmx", viewModel.state.value.searchQuery)
    }

    @Test
    fun `processIntent ToggleFavorite inverts showOnlyFavorites in state`() = runTest(dispatcher) {
        // initial state
        assertFalse(viewModel.state.value.showOnlyFavorites)

        // when
        viewModel.processIntent(CitiesIntent.ToggleFavorite)

        // then
        assertTrue(viewModel.state.value.showOnlyFavorites)
    }

    @Test
    fun `paginatedCities emits PagingData when state changes`() = runTest(dispatcher) {
        // when
        viewModel.processIntent(CitiesIntent.Search("oaxaca"))
        advanceTimeBy(350)

        // then
        val result = viewModel.paginatedCities.value
        assertIs<PagingData<City>>(result)
    }
}
