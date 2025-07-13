package com.oscarp.citiesapp.data.remote

import io.ktor.client.HttpClient

expect class KtorHttpClientProvider() {
    fun create(): HttpClient
}
