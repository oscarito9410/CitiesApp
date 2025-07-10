package com.oscarp.citiesapp.data.di

import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.local.getCityDao
import com.oscarp.citiesapp.data.local.getRoomDatabase
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.data.remote.KtorHttpClientProvider
import com.oscarp.citiesapp.data.repositories.CityRepositoryImpl
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun dataModule(): Module = module {
    // database
    single { getRoomDatabase(get()) }
    single { getCityDao(get()) }

    // rest client
    single {
        Json { ignoreUnknownKeys = true }
    }

    single {
        get<KtorHttpClientProvider>().create()
    }
    single {
        CityApiService(client = get())
    }

    single<CityRepository> {
        CityRepositoryImpl(
            api = get<CityApiService>(),
            importer = get<CityDataImporter>(),
            ioDispatcher = Dispatchers.IO
        )
    }
}
