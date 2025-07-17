package com.oscarp.citiesapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.AnimatedPercentText
import com.oscarp.citiesapp.ui.components.PercentTextTag
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AnimatedPercentTextTest : RobolectricComposeTest() {
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
