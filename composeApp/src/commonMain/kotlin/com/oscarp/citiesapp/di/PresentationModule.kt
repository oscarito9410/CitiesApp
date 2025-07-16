package com.oscarp.citiesapp.di

import com.oscarp.citiesapp.synccities.SyncCitiesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

fun presentationModule(): Module = module {
    single<SyncCitiesViewModel> {
        SyncCitiesViewModel(
            get(),
            Dispatchers.IO
        )
    }
}
