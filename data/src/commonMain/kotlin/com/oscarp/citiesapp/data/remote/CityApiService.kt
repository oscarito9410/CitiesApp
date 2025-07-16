package com.oscarp.citiesapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel

interface CityApiService {
    suspend fun fetchCitiesStream(): ByteReadChannel
}

class CityApiServiceImpl(private val client: HttpClient) : CityApiService {
    override suspend fun fetchCitiesStream(): ByteReadChannel =
        client.get(
            BASE_URL
        ).bodyAsChannel()

    companion object {
        private const val BASE_URL = "https://citiesuala.pages.dev/cities.json.gz"
    }
}
