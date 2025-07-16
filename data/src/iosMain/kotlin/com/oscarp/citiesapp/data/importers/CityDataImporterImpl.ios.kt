package com.oscarp.citiesapp.data.importers

import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.mapEntity
import com.oscarp.citiesapp.data.remote.CityDownloadDto
import com.oscarp.citiesapp.data.remote.CityDto
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json

class CityDataImporterImpl(
    private val cityDao: CityDao,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : CityDataImporter {

    override fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<CityDownloadDto> = flow {
        val bytes = channel.readRemaining().readByteArray()
        val jsonString = bytes.decodeToString()

        val allCities: List<CityDto> = json.decodeFromString(jsonString)
        val totalCities = allCities.size
        var totalInserted = 0

        allCities.chunked(chunkSize).forEach { batch ->
            cityDao.insertCities(batch.map { it.mapEntity() })
            totalInserted += batch.size
            emit(
                CityDownloadDto(
                    totalCities = totalCities,
                    totalInserted = totalInserted
                )
            )
        }
    }.catch { e ->
        Logger.e("CityDataImporter", e) { "Error during seeding cities" }
        throw e
    }.flowOn(Dispatchers.IO)
}
