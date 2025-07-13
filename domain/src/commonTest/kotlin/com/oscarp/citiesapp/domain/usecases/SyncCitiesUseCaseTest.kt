package com.oscarp.citiesapp.domain.usecases

import app.cash.turbine.test
import com.oscarp.citiesapp.domain.models.SyncProgress
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
            // onStart
            assertEquals(SyncProgress.Started, awaitItem())
            // first map
            assertEquals(SyncProgress.Inserting(5), awaitItem())
            // second map
            assertEquals(SyncProgress.Inserting(10), awaitItem())
            // onCompletion
            assertEquals(SyncProgress.Completed, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits start completed then error when repository fails`() = runTest(dispatcher) {
        // given
        val ex = IllegalStateException("network error")
        every { repository.syncCities() } returns flow {
            throw ex
        }

        // when & then
        useCase().test {
            // onStart
            assertEquals(SyncProgress.Started, awaitItem())
            val errorProgress = awaitItem()
            assertTrue(errorProgress is SyncProgress.Error) // catch
            assertEquals("network error", errorProgress.throwable.message)
            awaitComplete()
        }
    }
}
