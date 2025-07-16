package com.oscarp.citiesapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.ui.components.AnimatedPercentText
import com.oscarp.citiesapp.ui.components.PercentTextTag
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test

@Config(
    instrumentedPackages = ["androidx.loader.content"],
    sdk = [34]
)
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalTestApi::class)
class AnimatedPercentTextTest {

    @Test
    fun percent_is_displayed_correctly() = runComposeUiTest {
        // given
        setContent {
            AnimatedPercentText(percent = 45)
        }

        // then
        onNodeWithTag(PercentTextTag)
            .assertIsDisplayed()
            .assertTextEquals("45%")
    }
}
