package com.oscarp.citiesapp.data.repositories

import app.cash.turbine.test
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.data.remote.CityDownloadDto
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CityRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()

    // mocks
    private val api: CityApiService = mock()
    private val importer: CityDataImporter = mock()
    private val cityDao: CityDao = mock()

    private val repo = CityRepositoryImpl(
        api = api,
        importer = importer,
        cityDao = cityDao,
        ioDispatcher = dispatcher
    )

    @Test
    fun `syncCities emits importer values in order`() = runTest(dispatcher) {
        // given
        everySuspend { api.fetchCitiesStream() } returns ByteReadChannel("[]".toByteArray())
        every { importer.seedFromStream(any(), any()) } returns flowOf(
            CityDownloadDto(
                totalCities = 20,
                totalInserted = 5
            ),
            CityDownloadDto(
                totalCities = 20,
                totalInserted = 15
            ),
            CityDownloadDto(
                totalCities = 20,
                totalInserted = 20
            )
        )

        // when & then
        repo.syncCities().test {
            assertEquals(5, awaitItem().totalInserted)
            assertEquals(15, awaitItem().totalInserted)
            awaitItem().apply {
                assertEquals(20, totalInserted)
                assertEquals(20, totalCities)
            }
            awaitComplete()
        }
    }

    @Test
    fun `syncCities swallows importer exceptions and completes`() = runTest(dispatcher) {
        // given
        everySuspend { api.fetchCitiesStream() } returns ByteReadChannel("[]".toByteArray())

        every { importer.seedFromStream(any(), any()) } returns flow {
            throw IllegalStateException("network error")
        }

        // when & then
        repo.syncCities().test {
            val resultError = awaitError()
            assertEquals("network error", resultError.message)
        }
    }

    @Test
    fun `hasSyncCities returns true when getCityCount is higher than 0`() = runTest(dispatcher) {
        // given
        everySuspend { cityDao.getCitiesCount() } returns 100

        // when
        val hasSyncCities = repo.hasSyncCities()

        assertTrue(hasSyncCities)
    }

    @Test
    fun `hasSyncCities returns true when getCityCount is 0`() = runTest(dispatcher) {
        // given
        everySuspend { cityDao.getCitiesCount() } returns 0

        // when
        val hasSyncCities = repo.hasSyncCities()

        assertFalse(hasSyncCities)
    }
}
