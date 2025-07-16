package com.oscarp.citiesapp.di

import com.oscarp.citiesapp.domain.di.DispatchersQualifier
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun presentationModule(): Module = module {
    single<SyncCitiesViewModel> {
        SyncCitiesViewModel(
            get(),
            get(
                named(
                    DispatchersQualifier.IO
                )
            )
        )
    }
}
