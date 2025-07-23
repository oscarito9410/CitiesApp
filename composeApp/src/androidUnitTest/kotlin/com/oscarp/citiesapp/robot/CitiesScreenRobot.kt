@file:OptIn(ExperimentalTestApi::class)

package com.oscarp.citiesapp.robot

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.oscarp.citiesapp.features.cities.AppendLoadingIndicatorTag
import com.oscarp.citiesapp.features.cities.CitiesCoordinator
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.cities.CitiesViewModel
import com.oscarp.citiesapp.features.cities.CityMapDetailTag
import com.oscarp.citiesapp.features.cities.EmptyCitiesListTag
import com.oscarp.citiesapp.features.cities.EmptyCitySelectedTag
import com.oscarp.citiesapp.features.cities.RefreshLoadingIndicatorTag
import com.oscarp.citiesapp.features.cities.SingleColumnCitiesListTag
import com.oscarp.citiesapp.ui.components.CityItemTag
import com.oscarp.citiesapp.ui.components.FavoriteButtonTag
import com.oscarp.citiesapp.ui.components.SearchFavoritesSwitchTag
import com.oscarp.citiesapp.ui.components.SearchTextFieldTag
import com.oscarp.citiesapp.ui.theme.AppTheme

class CitiesScreenRobot(
    private val composeTestRule: ComposeUiTest,
    private val viewModel: CitiesViewModel,
    private val coordinator: CitiesCoordinator,
    private val hostState: SnackbarHostState? = null
) {
    fun setScreenContent() {
        composeTestRule.setContent {
            AppTheme {
                CitiesScreen(
                    viewModel = viewModel,
                    coordinator = coordinator,
                    hostState = hostState
                )
            }
        }
    }

    fun enterSearchQuery(query: String) {
        composeTestRule.onNodeWithTag(SearchTextFieldTag).performTextInput(query)
    }

    fun toggleFavoritesSwitch() {
        composeTestRule.onNodeWithTag(SearchFavoritesSwitchTag).performClick()
    }

    fun clickCityItem(cityId: Long) {
        composeTestRule.onNodeWithTag("${CityItemTag}_$cityId").performClick()
    }

    fun clickFavoriteButton() {
        composeTestRule.onNodeWithTag(FavoriteButtonTag).performClick()
    }

    fun assertCityItemDisplayed(cityId: Long) {
        composeTestRule.onNodeWithTag("${CityItemTag}_$cityId").assertIsDisplayed()
    }

    fun assertFavoriteButtonDisplayed() {
        composeTestRule.onNodeWithTag(FavoriteButtonTag).assertIsDisplayed()
    }

    fun assertSearchTextFieldDisplayed() {
        composeTestRule.onNodeWithTag(SearchTextFieldTag).assertIsDisplayed()
    }

    fun assertCityMapDetailNotDisplayed() {
        composeTestRule.onNodeWithTag(CityMapDetailTag).assertIsNotDisplayed()
    }

    fun assertMapRenderTextDisplayed() {
        composeTestRule.onNodeWithText(MAP_RENDER_TEXT_ASSERT).assertIsDisplayed()
    }

    fun assertEmptyCitySelectedTextDisplayed() {
        composeTestRule.onNodeWithTag(EmptyCitySelectedTag).assertIsDisplayed()
    }

    fun assertEmptyCitiesListDisplayed() {
        composeTestRule.onNodeWithTag(EmptyCitiesListTag).assertIsDisplayed()
    }

    fun assertRefreshLoadingIndicatorDisplayed() {
        composeTestRule.onNodeWithTag(RefreshLoadingIndicatorTag).assertIsDisplayed()
    }

    fun assertAppendLoadingIndicatorDisplayed() {
        composeTestRule.onNodeWithTag(AppendLoadingIndicatorTag).assertIsDisplayed()
    }

    fun assertErrorTextDisplayed(errorMessage: String) {
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    fun assertSingleColumnListDisplayed() {
        composeTestRule.onNodeWithTag(SingleColumnCitiesListTag).assertIsDisplayed()
    }

    fun assertGenericErrorDisplayed() {
        composeTestRule.onNodeWithText(GENERIC_ERROR_VALIDATION).assertIsDisplayed()
    }

    companion object {
        private const val GENERIC_ERROR_VALIDATION = "Generic error"
        private const val MAP_RENDER_TEXT_ASSERT = "Map render in test"
    }
}
