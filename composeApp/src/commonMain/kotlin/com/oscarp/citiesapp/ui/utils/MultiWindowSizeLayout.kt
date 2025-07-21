package com.oscarp.citiesapp.ui.utils

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import co.touchlab.kermit.Logger

/**
 * A utility function to display different composable layouts based on the current window size class.
 *
 * This function acts as a wrapper around [WindowSizeClass.display], simplifying the process of defining
 * layouts for various screen sizes and orientations.
 *
*/
@Composable
fun MultiWindowSizeLayout(
    content: @Composable (DeviceLayoutMode) -> Unit
) {
    val log = Logger.withTag("WindowSizeClass")
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val layoutMode =
        remember(windowSizeClass.windowWidthSizeClass, windowSizeClass.windowHeightSizeClass) {
            when {
                windowSizeClass.isPortraitPhone() -> DeviceLayoutMode.SINGLE_PANE
                windowSizeClass.isPortraitTablet() -> DeviceLayoutMode.TWO_PANE
                windowSizeClass.isIPadLandscape() -> DeviceLayoutMode.TWO_PANE
                windowSizeClass.isLandscapePhone() -> DeviceLayoutMode.TWO_PANE
                windowSizeClass.isFullExpanded() -> DeviceLayoutMode.TWO_PANE
                else -> DeviceLayoutMode.SINGLE_PANE
            }
        }

    log.v {
        "Layout: Default - Width: ${windowSizeClass.windowWidthSizeClass}," +
            " Height: ${windowSizeClass.windowHeightSizeClass}"
    }

    content(layoutMode)
}

private fun WindowSizeClass.isPortraitPhone() =
    windowWidthSizeClass == WindowWidthSizeClass.COMPACT &&
        windowHeightSizeClass in listOf(
            WindowHeightSizeClass.MEDIUM,
            WindowHeightSizeClass.EXPANDED
        )

private fun WindowSizeClass.isPortraitTablet() =
    windowWidthSizeClass == WindowWidthSizeClass.MEDIUM &&
        windowHeightSizeClass == WindowHeightSizeClass.EXPANDED

private fun WindowSizeClass.isIPadLandscape() =
    windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
        windowHeightSizeClass == WindowHeightSizeClass.MEDIUM

private fun WindowSizeClass.isLandscapePhone() =
    windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
        windowHeightSizeClass == WindowHeightSizeClass.COMPACT

private fun WindowSizeClass.isFullExpanded() =
    windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
        windowHeightSizeClass == WindowHeightSizeClass.EXPANDED
