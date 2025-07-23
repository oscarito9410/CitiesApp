@file:OptIn(ExperimentalTestApi::class)

package com.oscarp.citiesapp.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.oscarp.citiesapp.features.synccities.SyncCitiesCoordinator
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncScreen
import com.oscarp.citiesapp.features.synccities.TagCompleted
import com.oscarp.citiesapp.features.synccities.TagLoading
import com.oscarp.citiesapp.features.synccities.TagNotInternet
import com.oscarp.citiesapp.features.synccities.TagRetryButton
import com.oscarp.citiesapp.ui.theme.AppTheme

class SyncScreenRobot(
    private val composeTestRule: ComposeUiTest,
    private val coordinator: SyncCitiesCoordinator,
    private val viewModel: SyncCitiesViewModel
) {

    fun setScreenContent() {
        composeTestRule.setContent {
            AppTheme {
                SyncScreen(
                    viewModel = viewModel,
                    coordinator = coordinator
                )
            }
        }
    }

    fun assertLoadingVisible() {
        composeTestRule.onNodeWithTag(TagLoading).assertIsDisplayed()
    }

    fun assertNoInternetVisible() {
        composeTestRule.onNodeWithTag(TagNotInternet).assertIsDisplayed()
    }

    fun clickRetryButton() {
        composeTestRule.onNodeWithTag(TagRetryButton).performClick()
    }

    fun assertCompletedVisible() {
        composeTestRule.onNodeWithTag(TagCompleted).assertIsDisplayed()
    }
}
