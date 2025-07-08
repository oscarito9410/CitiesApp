package com.oscarp.citiesapp.di

import com.oscarp.citiesapp.data.di.dataModule
import com.oscarp.citiesapp.data.di.platformModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    appDeclaration: KoinAppDeclaration = {}
) =
    startKoin {
        appDeclaration()
        modules(
            dataModule(),
            platformModule()
        )
    }

// called by IOS
fun KoinApplication.Companion.start(): KoinApplication = initKoin { }