package com.oscarp.citiesapp.domain.di

import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase
import com.oscarp.citiesapp.domain.usecases.HasSyncCitiesUseCase
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

fun domainModule(): Module = module {
    single {
        SyncCitiesUseCase(
            repository = get()
        )
    }
    single {
        HasSyncCitiesUseCase(
            repository = get()
        )
    }
    single {
        GetPaginatedCitiesUseCase(
            factory = get()
        )
    }
}
