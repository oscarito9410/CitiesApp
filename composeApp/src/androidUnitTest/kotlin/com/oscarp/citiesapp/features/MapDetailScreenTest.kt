@file:OptIn(ExperimentalTestApi::class)

package com.oscarp.citiesapp.features

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.features.mapdetail.BackContentDescription
import com.oscarp.citiesapp.features.mapdetail.MapDetailScreen
import com.oscarp.citiesapp.mappers.toCityMapDetail
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.theme.AppTheme
import org.junit.Test

class MapDetailScreenTest : RobolectricComposeTest() {

    private val fakeCity = City(
        id = 1L,
        name = "Test City",
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = false,
        country = "MX"
    )

    @Test
    fun map_details_screen_displays_map_and_city_details() =
        runComposeUiTest {
            setContent {
                AppTheme {
                    MapDetailScreen(fakeCity.toCityMapDetail()) {
                    }
                }
            }
            // city name, back button and map are shown
            onNodeWithText(fakeCity.name).assertIsDisplayed()
            onNodeWithContentDescription(BackContentDescription).assertIsDisplayed()
            onNodeWithText(
                "Map render in test",
            ).assertIsDisplayed()
        }
}
