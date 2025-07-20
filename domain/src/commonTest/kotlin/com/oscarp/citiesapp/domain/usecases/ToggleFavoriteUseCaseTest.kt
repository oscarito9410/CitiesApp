@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.domain.usecases

import com.oscarp.citiesapp.domain.exceptions.CityNotFoundException
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val cityRepository: CityRepository = mock()
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        toggleFavoriteUseCase = ToggleFavoriteUseCase(cityRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke returns true when repository toggle is successful`() = runTest(dispatcher) {
        // given
        val cityId = 1L
        everySuspend { cityRepository.toggleFavorite(cityId) } returns true

        // when
        val result = toggleFavoriteUseCase(cityId)

        // then
        assertTrue(result, "Use case should return true when repository succeeds.")
        verifySuspend { cityRepository.toggleFavorite(cityId) }
    }

    @Test
    fun `invoke returns false when repository toggle fails`() = runTest(dispatcher) {
        // given
        val cityId = 2L
        everySuspend { cityRepository.toggleFavorite(cityId) } returns false

        // when
        val result = toggleFavoriteUseCase(cityId)

        // then
        assertFalse(result, "Use case should return false when repository fails.")
        verifySuspend { cityRepository.toggleFavorite(cityId) }
    }

    @Test
    fun `invoke propagates exceptions from repository`() = runTest(dispatcher) {
        // given
        val cityId = 3L
        val exception = CityNotFoundException("city not found")
        everySuspend { cityRepository.toggleFavorite(cityId) } throws exception

        // when & then
        val thrownException = try {
            toggleFavoriteUseCase(cityId)
            null // Should not be reached
        } catch (e: CityNotFoundException) {
            e
        }

        assertTrue(thrownException is CityNotFoundException, "Exception should be propagated.")
        verifySuspend { cityRepository.toggleFavorite(cityId) }
    }
}
