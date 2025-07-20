package com.oscarp.citiesapp.data.importers

import com.oscarp.citiesapp.data.remote.dtos.CityDownloadDto
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow

interface CityDataImporter {
    /**
     * reads from the provided channel, parses cityDto items and inserts them in size batches.
     * @param chunkSize  The maximum number of city entries to insert in a single transaction.
     *                   Batching reduces memory pressure and improves write performance.
     * @return A [Flow] of [CityDownloadDto] values, where each emission is the total count of
     * items inserted up to that point.
     */
    fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<CityDownloadDto>
}
