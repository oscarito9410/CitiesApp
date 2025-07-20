@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.data.repositories

import app.cash.turbine.test
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.local.entities.CityEntity
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.data.remote.dtos.CityDownloadDto
import com.oscarp.citiesapp.domain.exceptions.CityNotFoundException
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

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
    fun `syncCities propagates importer exceptions`() = runTest(dispatcher) {
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

    @Test
    fun `syncCities calls api and importer and emits importer values`() = runTest(dispatcher) {
        // given
        val byteReadChannel = ByteReadChannel("[]".toByteArray())
        everySuspend { api.fetchCitiesStream() } returns byteReadChannel
        every { importer.seedFromStream(any(), any()) } returns flowOf()

        // when
        repo.syncCities().test {
            awaitComplete()
        }

        // then (verify interactions)
        verifySuspend { api.fetchCitiesStream() }
        verify { importer.seedFromStream(eq(byteReadChannel), any()) } // `eq` for specific instance
    }

    @Test
    fun `getPaginatedCities calls no-search DAO method and maps results`() = runTest(dispatcher) {
        // given
        val dummyEntities = listOf(
            CityEntity(
                1,
                "Querétaro",
                "MX",
                0.0,
                0.0
            ),
            CityEntity(
                2,
                "Puebla",
                "MX",
                0.0,
                0.0
            ),
        )
        everySuspend {
            cityDao.getPaginatedCitiesNoSearch(
                onlyFavorites = false,
                loadSize = 2,
                offset = 0
            )
        } returns dummyEntities

        // when
        val result = repo.getPaginatedCities(
            page = 0,
            loadSize = 2,
            searchQuery = "",
            onlyFavorites = false
        )

        // then
        assertEquals(2, result.size)
        assertEquals("Querétaro", result[0].name)
        assertEquals("Puebla", result[1].name)
    }

    @Test
    fun `getPaginatedCities calls search DAO method and maps results`() = runTest(dispatcher) {
        // given
        val dummyEntities = listOf(
            CityEntity(
                1,
                "Querétaro",
                "MX",
                0.0,
                0.0
            ),
            CityEntity(
                2,
                "Quito",
                "EC",
                0.0,
                0.0
            ),
        )
        everySuspend {
            cityDao.getPaginatedCitiesWithSearch(
                query = "q",
                onlyFavorites = true,
                loadSize = 10,
                offset = 10
            )
        } returns dummyEntities

        // when
        val result = repo.getPaginatedCities(
            page = 1,
            loadSize = 10,
            searchQuery = "q",
            onlyFavorites = true
        )

        // then
        assertEquals(2, result.size)
        assertTrue(result.all { it.displayName.lowercase().startsWith("q") })
    }

    @Test
    fun `toggleFavorite returns true when city exists and update is successful`() =
        runTest(dispatcher) {
            // given
            val cityId = 1L
            val cityEntity = CityEntity(cityId, "Test City", "TC", 0.0, 0.0, isFavorite = false)
            everySuspend { cityDao.getCityById(cityId) } returns cityEntity
            everySuspend { cityDao.updateFavoriteStatus(cityId, true) } returns 1 // 1 row updated

            // when
            val result = repo.toggleFavorite(cityId)

            // then
            assertTrue(result)
            verifySuspend { cityDao.getCityById(eq(cityId)) }
            verifySuspend { cityDao.updateFavoriteStatus(eq(cityId), eq(true)) }
        }

    @Test
    fun `toggleFavorite returns false when update fails`() = runTest(dispatcher) {
        // given
        val cityId = 1L
        val cityEntity = CityEntity(cityId, "Test City", "TC", 0.0, 0.0, isFavorite = false)
        everySuspend { cityDao.getCityById(cityId) } returns cityEntity
        everySuspend { cityDao.updateFavoriteStatus(cityId, true) } returns 0 // 0 rows updated

        // when
        val result = repo.toggleFavorite(cityId)

        // then
        assertFalse(result)
    }

    @Test
    fun `toggleFavorite throws CityNotFoundException when city does not exist`() =
        runTest(dispatcher) {
            // given
            val cityId = 99L
            everySuspend { cityDao.getCityById(cityId) } returns null

            // when & then
            val exception = try {
                repo.toggleFavorite(cityId)
                null
            } catch (e: Exception) {
                e
            }

            assertTrue(exception is CityNotFoundException)
            verifySuspend(mode = VerifyMode.not) {
                cityDao.updateFavoriteStatus(any(), any())
            }
        }

    @Test
    fun `getFavoriteCitiesIds returns a flow of favorite city IDs`() = runTest(dispatcher) {
        // given
        val favoriteCitiesFlow = flowOf(
            listOf(1L, 3L)
        )
        every { cityDao.getFavoriteCitiesIdsFlow() } returns favoriteCitiesFlow

        // when & then
        repo.getFavoriteCitiesIds().test {
            val favoriteIds = awaitItem()
            assertEquals(setOf(1L, 3L), favoriteIds)
            awaitComplete()
        }
    }
}
