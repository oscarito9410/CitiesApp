package com.oscarp.citiesapp.data.repositories

import app.cash.turbine.test
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.remote.CityApiService
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CityRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()

    // mocks
    private val api: CityApiService = mock()
    private val importer: CityDataImporter = mock()

    private val repo = CityRepositoryImpl(
        api = api,
        importer = importer,
        ioDispatcher = dispatcher
    )

    @Test
    fun `syncCities emits importer values in order`() = runTest(dispatcher) {
        // given
        everySuspend { api.fetchCitiesStream() } returns ByteReadChannel("[]".toByteArray())
        every { importer.seedFromStream(any(), any()) } returns flowOf(
            5,
            15,
            20
        )

        // when & then
        repo.syncCities().test {
            assertEquals(5, awaitItem())
            assertEquals(15, awaitItem())
            assertEquals(20, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `syncCities swallows importer exceptions and completes`() = runTest(dispatcher) {
        // given
        everySuspend { api.fetchCitiesStream() } returns ByteReadChannel("[]".toByteArray())

        // when
        every { importer.seedFromStream(any(), any()) } returns flow {
            throw IllegalStateException("network error")
        }

        // then
        repo.syncCities().test {
            val resultError = awaitError()
            assertEquals("network error", resultError.message)
        }
    }
}
