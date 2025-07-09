package com.oscarp.citiesapp.data.di

import org.koin.core.module.Module
import org.koin.dsl.module

fun dataModule(): Module = module {
    single<String> { "hello world" }
}
