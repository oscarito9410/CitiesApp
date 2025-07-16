package com.importers

import app.cash.turbine.test
import com.oscarp.citiesapp.data.importers.CityDataImporterImpl
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.mapEntity
import com.oscarp.citiesapp.data.remote.CityDto
import com.oscarp.citiesapp.data.remote.CoordDto
import io.ktor.utils.io.ByteReadChannel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CityDataImporterImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    // build a JSON array of n distinct CityDto objects
    private fun makeJson(n: Int): String {
        val list = (1..n).map {
            CityDto(
                id = it.toLong(),
                name = "City$it",
                country = "C$it",
                coord = CoordDto(lat = it.toDouble(), lon = it.toDouble())
            )
        }
        return json.encodeToString(ListSerializer(CityDto.serializer()), list)
    }

    @Test
    fun `seedFromStream emits cumulative totals and calls dao in correct chunks`() = runTest {
        // given a JSON of 5 items, and chunkSize=2 => batches of [1,2], [3,4], [5]
        val channel = ByteReadChannel(makeJson(5).toByteArray())
        val dao: CityDao = mockk()
        coEvery { dao.insertCities(any()) } returns Unit

        val importer = CityDataImporterImpl(
            cityDao = dao,
            json = json,
            ioDispatcher = UnconfinedTestDispatcher()
        )

        // when & then: expect cumulative totals 2, 4, and 5
        importer.seedFromStream(channel, chunkSize = 2).test {
            assert(awaitItem().totalInserted == 2)
            assert(awaitItem().totalInserted == 4)
            awaitItem().apply {
                assert(totalInserted == 5)
                assert(totalCities == 5)
            }
            awaitComplete()
        }

        // verify DAO received exactly the right batches (converted to entities)
        coVerify(exactly = 1) {
            dao.insertCities(
                listOf(1, 2).map {
                    CityDto(
                        it.toLong(),
                        "City$it",
                        "C$it",
                        CoordDto(it.toDouble(), it.toDouble())
                    ).mapEntity()
                }
            )
        }
        coVerify(exactly = 1) {
            dao.insertCities(
                listOf(3, 4).map {
                    CityDto(
                        it.toLong(),
                        "City$it",
                        "C$it",
                        CoordDto(it.toDouble(), it.toDouble())
                    ).mapEntity()
                }
            )
        }
        coVerify(exactly = 1) {
            dao.insertCities(
                listOf(5).map {
                    CityDto(
                        it.toLong(),
                        "City$it",
                        "C$it",
                        CoordDto(it.toDouble(), it.toDouble())
                    ).mapEntity()
                }
            )
        }
    }

    @Test
    fun `seedFromStream emits nothing when JSON list is empty`() = runTest {
        // given an empty JSON list
        val channel = ByteReadChannel("[]".toByteArray())
        val dao: CityDao = mockk(relaxed = true)

        val importer = CityDataImporterImpl(dao, json, UnconfinedTestDispatcher())

        // when & then: no emissions at all
        importer.seedFromStream(channel, chunkSize = 3).test {
            awaitComplete()
        }

        // DAO should never be called
        coVerify(exactly = 0) {
            dao.insertCities(any())
        }
    }
}
