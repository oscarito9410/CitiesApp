package com.oscarp.citiesapp.data.importers

import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.mapEntity
import com.oscarp.citiesapp.data.remote.CityDownloadDto
import com.oscarp.citiesapp.data.remote.CityDto
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class CityDataImporterImpl(
    private val cityDao: CityDao,
    private val json: Json = Json { ignoreUnknownKeys = true },
    private val ioDispatcher: CoroutineDispatcher
) : CityDataImporter {

    @OptIn(ExperimentalSerializationApi::class)
    override fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<CityDownloadDto> = flow {
        channel.toInputStream().use { input ->
            val allCities = json.decodeFromStream(
                ListSerializer(CityDto.serializer()),
                input
            )
            val total = allCities.size
            var totalInserted = 0
            allCities.chunked(chunkSize).forEach { chunk ->
                cityDao.insertCities(chunk.map { it.mapEntity() })
                totalInserted += chunk.size
                emit(
                    CityDownloadDto(
                        totalCities = total,
                        totalInserted = totalInserted
                    )
                )
            }
        }
    }.catch { e ->
        Logger.e("CityDataImporter", e) { "Error during seeding cities" }
        throw e
    }.flowOn(ioDispatcher)
}
