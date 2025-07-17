package com.oscarp.citiesapp.testutil

import android.content.ContentProvider
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(
    instrumentedPackages = ["androidx.loader.content"],
    sdk = [34]
)
@RunWith(RobolectricTestRunner::class)
abstract class RobolectricComposeTest {

    @Before
    fun setup() {
        setupAndroidContextProvider()
    }

    @After
    fun after() {
        GlobalContext.stopKoin()
    }

    // Configures Compose's AndroidContextProvider to access resources in tests.
    // See https://youtrack.jetbrains.com/issue/CMP-6612
    private fun setupAndroidContextProvider() {
        val type = findAndroidContextProvider() ?: return
        Robolectric.setupContentProvider(type)
    }

    private fun findAndroidContextProvider(): Class<ContentProvider>? {
        val providerClassName = "org.jetbrains.compose.resources.AndroidContextProvider"
        return try {
            @Suppress("UNCHECKED_CAST")
            Class.forName(providerClassName) as Class<ContentProvider>
        } catch (_: ClassNotFoundException) {
            // Logger.debug("Class not found: $providerClassName")
            // Tests that don't depend on Compose will not have the provider class in classpath and will get
            // ClassNotFoundException. Skip configuring the provider for them.
            null
        }
    }
}