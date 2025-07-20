package com.oscarp.citiesapp.data.remote.client

import io.ktor.client.plugins.logging.Logger
import co.touchlab.kermit.Logger as KermitLogger

class KtorLogger : Logger {
    override fun log(message: String) {
        KermitLogger.i(tag = "KtorClient", messageString = message)
    }
}
