package com.oscarp.citiesapp.data.repositories

import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.importers.TotalInserted
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CityRepositoryImpl(
    private val api: CityApiService,
    private val importer: CityDataImporter,
    private val ioDispatcher: CoroutineDispatcher
) : CityRepository {

    companion object {
        private const val CHUNK_SIZE = 10000
    }

    override fun syncCities(): Flow<TotalInserted> = flow {
        val channel = api.fetchCitiesStream()
        importer.seedFromStream(channel, CHUNK_SIZE)
            .catch {
                Logger.e("CityRepository", it)
            }
            .collect {
                emit(it)
            }
    }.flowOn(ioDispatcher)
}
