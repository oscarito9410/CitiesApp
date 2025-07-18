package com.oscarp.citiesapp.ui
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.FavoriteButton
import com.oscarp.citiesapp.ui.components.FavoriteButtonTag
import com.oscarp.citiesapp.ui.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FavoriteButtonTest : RobolectricComposeTest() {

    private fun fakeCity(isFavorite: Boolean) = City(
        id = 99,
        name = "Test City",
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = isFavorite,
        country = "TC"
    )

    @Test
    fun favorite_button_shows_filled_icon_when_favorite() = runComposeUiTest {
        // given
        setContent {
            AppTheme {
                FavoriteButton(
                    modifier = Modifier.testTag(FavoriteButtonTag),
                    city = fakeCity(true),
                    onToggleFavorite = {}
                )
            }
        }

        // then
        onNodeWithTag(FavoriteButtonTag)
            .assertIsDisplayed()
    }

    @Test
    fun favorite_button_shows_border_icon_when_not_favorite() = runComposeUiTest {
        // given
        setContent {
            AppTheme {
                FavoriteButton(
                    modifier = Modifier.testTag(FavoriteButtonTag),
                    city = fakeCity(false),
                    onToggleFavorite = {}
                )
            }
        }

        // then
        onNodeWithTag(FavoriteButtonTag)
            .assertIsDisplayed()
    }

    @Test
    fun clicking_favorite_icon_triggers_callback() = runComposeUiTest {
        var toggled = false

        // given
        setContent {
            AppTheme {
                FavoriteButton(
                    modifier = Modifier.testTag(FavoriteButtonTag),
                    city = fakeCity(true),
                    onToggleFavorite = { toggled = true }
                )
            }
        }

        // when
        onNodeWithTag(FavoriteButtonTag).performClick()

        // then
        assertTrue(toggled)
    }
}
