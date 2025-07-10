package com.oscarp.citiesapp.data.importers

import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow

typealias TotalInserted = Int

interface CityDataImporter {
    /**
     * Reads from the provided channel, parses CityDto items one by one,
     * and inserts them in size batches.
     * @param chunkSize  The maximum number of city entries to insert in a single transaction.
     *                   Batching reduces memory pressure and improves write performance.
     * @return A [Flow] of [TotalInserted] values, where each emission is the total count of
     * items inserted up to that point.
     */
    fun seedFromStream(
        channel: ByteReadChannel,
        chunkSize: Int
    ): Flow<TotalInserted>
}
