package com.oscarp.citiesapp.data.di

import androidx.room.RoomDatabase
import com.oscarp.citiesapp.data.local.AppDatabase
import com.oscarp.citiesapp.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
}
