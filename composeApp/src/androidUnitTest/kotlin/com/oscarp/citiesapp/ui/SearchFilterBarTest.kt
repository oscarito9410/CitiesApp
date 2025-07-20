package com.oscarp.citiesapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.SearchFavoritesLabelTag
import com.oscarp.citiesapp.ui.components.SearchFavoritesSwitchTag
import com.oscarp.citiesapp.ui.components.SearchFilterBar
import com.oscarp.citiesapp.ui.components.SearchTextFieldTag
import com.oscarp.citiesapp.ui.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SearchFilterBarTest : RobolectricComposeTest() {

    @Test
    fun search_bar_displays_textfield_and_switch() = runComposeUiTest {
        setContent {
            AppTheme {
                SearchFilterBar(
                    searchQuery = "",
                    showOnlyFavorites = false,
                    onSearchQueryChanged = {},
                    onShowFavoritesFilter = {}
                )
            }
        }

        onNodeWithTag(SearchTextFieldTag).assertIsDisplayed()
        onNodeWithTag(SearchFavoritesSwitchTag).assertIsDisplayed()
        onNodeWithTag(SearchFavoritesLabelTag).assertIsDisplayed()
    }

    @Test
    fun search_bar_text_input_works() = runComposeUiTest {
        var query = ""

        setContent {
            AppTheme {
                SearchFilterBar(
                    searchQuery = query,
                    showOnlyFavorites = false,
                    onSearchQueryChanged = { query = it },
                    onShowFavoritesFilter = {}
                )
            }
        }

        onNodeWithTag(SearchTextFieldTag).performTextInput("mexico")
        assertEquals("mexico", query)
    }

    @Test
    fun toggle_switch_triggers_callback() = runComposeUiTest {
        var toggled = false

        setContent {
            AppTheme {
                SearchFilterBar(
                    searchQuery = "",
                    showOnlyFavorites = false,
                    onSearchQueryChanged = {},
                    onShowFavoritesFilter = { toggled = true }
                )
            }
        }

        onNodeWithTag(SearchFavoritesSwitchTag).performClick()
        assertTrue(toggled)
    }
}
