package com.oscarp.citiesapp.data.di

import androidx.room.RoomDatabase
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.importers.CityDataImporterImpl
import com.oscarp.citiesapp.data.local.AppDatabase
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.local.getDatabaseBuilder
import com.oscarp.citiesapp.data.remote.client.KtorHttpClientProvider
import com.oscarp.citiesapp.domain.di.DispatchersQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(get())
    }

    single<KtorHttpClientProvider> {
        KtorHttpClientProvider()
    }

    single<CityDataImporter> {
        CityDataImporterImpl(
            cityDao = get<CityDao>(),
            json = get(),
            ioDispatcher = get(
                named(
                    DispatchersQualifier.IO
                )
            )
        )
    }
}
