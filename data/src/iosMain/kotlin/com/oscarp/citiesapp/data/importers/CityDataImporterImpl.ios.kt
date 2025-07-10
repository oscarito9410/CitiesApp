package com.oscarp.citiesapp.data.importers

import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.mapEntity
import com.oscarp.citiesapp.data.remote.CityDto
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class CityDataImporterIosImpl(
    private val cityDao: CityDao,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : CityDataImporter {

    @OptIn(ExperimentalSerializationApi::class)
    override fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<TotalInserted> = flow {
        val bytes = channel.readRemaining().readByteArray()
        val jsonString = bytes.decodeToString()

        val allCities: List<CityDto> = json.decodeFromString(jsonString)

        var total = 0
        allCities.chunked(chunkSize).forEach { batch ->
            cityDao.insertCities(batch.map { it.mapEntity() })
            total += batch.size
            emit(total)
        }
    }
        .flowOn(Dispatchers.IO)
}
