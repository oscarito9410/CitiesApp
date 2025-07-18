package com.oscarp.citiesapp.data.di

import com.oscarp.citiesapp.data.factory.CitiesPagingSourceFactoryImpl
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.local.getCityDao
import com.oscarp.citiesapp.data.local.getRoomDatabase
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.data.remote.CityApiServiceImpl
import com.oscarp.citiesapp.data.remote.KtorHttpClientProvider
import com.oscarp.citiesapp.data.repositories.CityRepositoryImpl
import com.oscarp.citiesapp.domain.di.DispatchersQualifier
import com.oscarp.citiesapp.domain.factory.CitiesPagingSourceFactory
import com.oscarp.citiesapp.domain.repositories.CityRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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
    single<CityApiService> {
        CityApiServiceImpl(client = get())
    }

    single<CityRepository> {
        CityRepositoryImpl(
            api = get<CityApiService>(),
            importer = get<CityDataImporter>(),
            cityDao = get<CityDao>(),
            ioDispatcher = get(
                named(
                    DispatchersQualifier.IO
                )
            )
        )
    }

    single<CitiesPagingSourceFactory> {
        CitiesPagingSourceFactoryImpl(
            repository = get()
        )
    }
}
