package com.oscarp.citiesapp

import android.app.Application
import com.oscarp.citiesapp.di.initKoin

class CitiesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
          //  androidLogger(level = Level.NONE)
          //  androidContext(androidContext = this@LoginApplication)
        }
    }
}