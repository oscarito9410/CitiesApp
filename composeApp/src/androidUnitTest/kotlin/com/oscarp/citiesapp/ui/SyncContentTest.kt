package com.oscarp.citiesapp.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.features.synccities.SyncContent
import com.oscarp.citiesapp.features.synccities.SyncViewState
import com.oscarp.citiesapp.features.synccities.TagCompleted
import com.oscarp.citiesapp.features.synccities.TagLoading
import com.oscarp.citiesapp.features.synccities.TagNotInternet
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.PercentTextTag
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SyncContentTest : RobolectricComposeTest() {

    @Test
    fun loadingState_showsLoadingContent_withPercent() = runComposeUiTest {
        val state = SyncViewState(
            isLoading = true,
            percentSync = 42,
            isError = false,
            isNoInternet = false,
            isCompleted = false
        )

        setContent {
            SyncContent(
                state = state,
                onRetry = {}
            )
        }

        onNodeWithTag(TagLoading).assertIsDisplayed()
        onNodeWithTag(PercentTextTag).assertTextEquals("42%")
    }

    @Test
    fun errorState_showsNoInternetContent() = runComposeUiTest {
        val state = SyncViewState(
            isLoading = false,
            percentSync = 0,
            isError = true,
            isNoInternet = true,
            isCompleted = false
        )

        setContent {
            SyncContent(
                state = state,
                onRetry = {}
            )
        }

        onNodeWithTag(TagNotInternet).assertIsDisplayed()
    }

    @Test
    fun completedState_showsCompletedContent() = runComposeUiTest {
        val state = SyncViewState(
            isLoading = false,
            percentSync = 100,
            isError = false,
            isNoInternet = false,
            isCompleted = true
        )

        setContent {
            SyncContent(
                state = state,
                onRetry = {}
            )
        }

        onNodeWithTag(TagCompleted).assertIsDisplayed()
    }
}
