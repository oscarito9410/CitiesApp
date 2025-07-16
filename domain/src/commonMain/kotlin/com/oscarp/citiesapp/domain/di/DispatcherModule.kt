package com.oscarp.citiesapp.domain.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun dispatcherModule() = module {
    single(named(DispatchersQualifier.MAIN)) {
        Dispatchers.Main
    }
    single(named(DispatchersQualifier.IO)) {
        Dispatchers.IO
    }
    single(named(DispatchersQualifier.DEFAULT)) {
        Dispatchers.Default
    }
    single(named(DispatchersQualifier.UNCONFINED)) {
        Dispatchers.Unconfined
    }
}

object DispatchersQualifier {
    const val MAIN = "Main"
    const val IO = "IO"
    const val DEFAULT = "Default"
    const val UNCONFINED = "Unconfined"
}
