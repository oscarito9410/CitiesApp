package com.oscarp.citiesapp.data.importers

import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.mapEntity
import com.oscarp.citiesapp.data.remote.CityDto
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class CityDataImporterAndroidImpl(
    private val cityDao: CityDao,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : CityDataImporter {

    @OptIn(ExperimentalSerializationApi::class)
    override fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<TotalInserted> = flow {
        channel.toInputStream().use { input ->
            json.decodeFromStream(ListSerializer(CityDto.serializer()), input)
                .chunked(chunkSize)
                .runningFold(0) { acc, batch ->
                    cityDao.insertCities(batch.map { it.mapEntity() })
                    acc + batch.size
                }
                .forEach { total ->
                    if (total > 0) {
                        emit(total)
                    }
                }
        }
    }.flowOn(Dispatchers.IO)
}
