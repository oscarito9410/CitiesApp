package com.oscarp.citiesapp.data.remote.client

import io.ktor.client.HttpClient

expect class KtorHttpClientProvider() {
    fun create(): HttpClient
}
