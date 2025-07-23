@file:OptIn(ExperimentalTestApi::class)

package com.oscarp.citiesapp.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.features.mapdetail.BackContentDescription
import com.oscarp.citiesapp.features.mapdetail.MapDetailCoordinator
import com.oscarp.citiesapp.features.mapdetail.MapDetailScreen
import com.oscarp.citiesapp.mappers.toCityMapDetail
import com.oscarp.citiesapp.ui.theme.AppTheme

class MapDetailScreenRobot(
    private val composeTestRule: ComposeUiTest,
    private val coordinator: MapDetailCoordinator,
    private val city: City,
) {
    fun setScreenContent() {
        composeTestRule.setContent {
            AppTheme {
                MapDetailScreen(
                    cityMapDetail = city.toCityMapDetail(),
                    coordinator = coordinator
                )
            }
        }
    }

    fun assertCityNameDisplayed() {
        composeTestRule.onNodeWithText(city.name).assertIsDisplayed()
    }

    fun assertBackButtonDisplayed() {
        composeTestRule.onNodeWithContentDescription(BackContentDescription).assertIsDisplayed()
    }

    fun assertMapDisplayed() {
        composeTestRule.onNodeWithText("Map render in test").assertIsDisplayed()
    }
}
