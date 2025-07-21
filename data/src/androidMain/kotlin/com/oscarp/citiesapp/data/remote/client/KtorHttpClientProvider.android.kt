package com.oscarp.citiesapp.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual class KtorHttpClientProvider actual constructor() {
    actual fun create(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(ContentEncoding) {
            gzip()
        }
        install(DefaultRequest) {
            header(HttpHeaders.AcceptEncoding, "gzip")
        }
        install(Logging) {
            logger = KtorLogger()
            level = LogLevel.HEADERS
        }
    }
}
