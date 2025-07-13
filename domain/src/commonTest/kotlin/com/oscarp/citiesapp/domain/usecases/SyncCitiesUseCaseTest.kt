package com.oscarp.citiesapp.domain.usecases

import app.cash.turbine.test
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncCitiesUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()
    private val repository: CityRepository = mock()
    private val useCase = SyncCitiesUseCase(repository)

    @Test
    fun `invoke emits start inserting items and completed in order`() = runTest(dispatcher) {
        // given
        every { repository.syncCities() } returns flowOf(5, 10)

        // when & then
        useCase().test {
            assertEquals(5, awaitItem())
            assertEquals(10, awaitItem())
            awaitComplete()
        }

        verify {
            repository.syncCities()
        }
    }

    @Test
    fun `invoke emits start completed then error when repository fails`() = runTest(dispatcher) {
        // given
        every { repository.syncCities() } returns flow {
            throw IllegalStateException("network error")
        }

        // when & then
        useCase().test {
            val resultError = awaitError()
            assertEquals("network error", resultError.message)
        }

        verify {
            repository.syncCities()
        }
    }
}
