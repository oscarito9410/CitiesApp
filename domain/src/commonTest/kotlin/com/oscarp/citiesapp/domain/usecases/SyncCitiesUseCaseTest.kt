@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.domain.usecases

import app.cash.turbine.test
import com.oscarp.citiesapp.domain.models.CityDownload
import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SyncCitiesUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()
    private val repository: CityRepository = mock()
    private val useCase = SyncCitiesUseCase(repository)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke emits start inserting items and completed in order`() = runTest(dispatcher) {
        // given
        every { repository.syncCities() } returns flowOf(
            CityDownload(
                totalCities = 10,
                totalInserted = 5
            ),
            CityDownload(
                totalCities = 10,
                totalInserted = 10
            )
        )

        // when & then
        useCase().test {
            assertEquals(
                5,
                awaitItem().totalInserted
            )
            awaitItem().apply {
                assertEquals(
                    10,
                    totalInserted
                )

                assertEquals(
                    10,
                    totalCities
                )
            }
            awaitComplete()
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
    }
}
