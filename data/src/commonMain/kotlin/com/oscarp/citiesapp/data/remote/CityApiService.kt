package com.oscarp.citiesapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel

class CityApiService(private val client: HttpClient) {
    suspend fun fetchCitiesStream(): ByteReadChannel =
        client.get(
            "${BASE_URL}/${CITIES_FILE_PATH}"
        )
            .bodyAsChannel()

    companion object {
        private const val BASE_URL = "https://gist.githubusercontent.com/hernan-uala"
        private const val CITIES_FILE_PATH = "dce8843a8edbe0b0018b32e137bc2b3a/raw/" +
                "0996accf70cb0ca0e16f9a99e0ee185fafca7af1/cities.json\""
    }
}
