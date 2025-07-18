package com.oscarp.citiesapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.CityItem
import com.oscarp.citiesapp.ui.components.CityItemTag
import com.oscarp.citiesapp.ui.components.FavoriteButtonTag
import com.oscarp.citiesapp.ui.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CityItemTest : RobolectricComposeTest() {

    private val testCity = City(
        id = 1L,
        name = "Ciudad de México",
        latitude = 19.43,
        longitude = -99.13,
        isFavorite = true,
        country = "MX"
    )

    @Test
    fun city_item_displays_city_name_and_favorite_icon() = runComposeUiTest {
        // given
        setContent {
            AppTheme {
                CityItem(
                    city = testCity,
                    onCityClicked = {},
                    onToggleFavorite = {}
                )
            }
        }

        // then
        onNodeWithTag(CityItemTag)
            .assertIsDisplayed()

        onNodeWithTag(FavoriteButtonTag)
            .assertIsDisplayed()
    }

    @Test
    fun clicking_favorite_button_triggers_callback() = runComposeUiTest {
        var wasClicked = false

        // given
        setContent {
            AppTheme {
                CityItem(
                    city = testCity,
                    onCityClicked = {},
                    onToggleFavorite = { wasClicked = true }
                )
            }
        }

        // when
        onNodeWithTag(FavoriteButtonTag).performClick()

        // then
        assertTrue(wasClicked)
    }

    @Test
    fun clicking_city_card_triggers_city_click() = runComposeUiTest {
        var wasClicked = false

        // given
        setContent {
            AppTheme {
                CityItem(
                    city = testCity,
                    onCityClicked = { wasClicked = true },
                    onToggleFavorite = {}
                )
            }
        }

        // when
        onNodeWithTag(CityItemTag).performClick()

        // then
        assertTrue(wasClicked)
    }
}
