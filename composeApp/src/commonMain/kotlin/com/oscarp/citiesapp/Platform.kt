package com.oscarp.citiesapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
