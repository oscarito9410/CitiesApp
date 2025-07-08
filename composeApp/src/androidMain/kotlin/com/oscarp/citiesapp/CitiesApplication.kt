package com.oscarp.citiesapp

import android.app.Application
import com.oscarp.citiesapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class CitiesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger(level = Level.INFO)
            androidContext(androidContext = this@CitiesApplication)
        }
    }
}
