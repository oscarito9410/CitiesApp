package com.oscarp.citiesapp.data.repositories

import co.touchlab.kermit.Logger
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.mappers.toDomain
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.models.CityDownload
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CityRepositoryImpl(
    private val api: CityApiService,
    private val importer: CityDataImporter,
    private val cityDao: CityDao,
    private val ioDispatcher: CoroutineDispatcher
) : CityRepository {

    private val logger: Logger = Logger.withTag("CityRepository")

    override suspend fun hasSyncCities(): Boolean {
        return withContext(ioDispatcher) {
            cityDao.getCitiesCount() > 0
        }
    }

    override suspend fun getPaginatedCities(
        page: Int,
        loadSize: Int,
        searchQuery: String,
        onlyFavorites: Boolean
    ): List<City> {
        return withContext(ioDispatcher) {
            val offset = page * loadSize
            logger.i { "getting paginated cities for page $page, query $searchQuery and onlyFavorites $onlyFavorites" }
            if (searchQuery.isBlank()) {
                cityDao.getPaginatedCitiesNoSearch(
                    onlyFavorites = onlyFavorites,
                    loadSize = loadSize,
                    offset = offset
                )
            } else {
                cityDao.getPaginatedCitiesWithSearch(
                    query = searchQuery,
                    onlyFavorites = onlyFavorites,
                    loadSize = loadSize,
                    offset = offset
                )
            }.map { it.toDomain() }
        }
    }

    override fun syncCities(): Flow<CityDownload> = flow {
        val channel = api.fetchCitiesStream()
        importer.seedFromStream(channel, CHUNK_CITIES_SIZE)
            .catch {
                logger.e(
                    throwable = it,
                    message = { "error fetching cities stream" }
                )
                throw it
            }
            .collect {
                emit(
                    CityDownload(
                        it.totalCities,
                        it.totalInserted
                    )
                )
            }
    }.flowOn(ioDispatcher)

    companion object {
        const val CHUNK_CITIES_SIZE = 20000
    }
}
