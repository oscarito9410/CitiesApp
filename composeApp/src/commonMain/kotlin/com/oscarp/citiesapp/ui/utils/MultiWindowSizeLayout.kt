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
 * @param default The default composable to display when no specific size class conditions are met.
 * @param expanded (Optional) The composable to display for large screen devices in landscape or desktop-like layouts.
 *                 Defaults to the [default] composable if not provided.
 * @param portrait (Optional) The composable to display for portrait configurations.
 * Defaults to the [default] composable if not provided.
 * @param portraitTablet (Optional) The composable to display for portrait tablet configurations.
 * @param portraitPhone (Optional) The composable to display for portrait phone configurations.
 */

@Composable
fun MultiWindowSizeLayout(
    default: @Composable () -> Unit,
    expanded: (@Composable () -> Unit)? = null,
    portrait: (@Composable () -> Unit)? = null,
    portraitTablet: (@Composable () -> Unit)? = null,
    portraitPhone: (@Composable () -> Unit)? = null,
    landscapePhone: (@Composable () -> Unit)? = null
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    windowSizeClass.Display(
        default,
        expanded ?: default,
        portrait ?: default,
        portraitTablet,
        portraitPhone,
        landscapePhone
    )
}

@Composable
fun MultiWindowSizeLayout(
    content: @Composable (DeviceLayoutMode) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val layoutMode =
        remember(windowSizeClass.windowWidthSizeClass, windowSizeClass.windowHeightSizeClass) {
            when {
                windowSizeClass.isPortraitPhone() -> DeviceLayoutMode.SINGLE_PANE
                else -> DeviceLayoutMode.TWO_PANE
            }
        }
    content(layoutMode)
}

/**
 * Displays a composable layout based on the current [WindowSizeClass] configuration.
 *
 * This function selects and invokes a composable lambda corresponding to the device's width and height
 * size classes. It is particularly useful for adapting layouts to different screen sizes, such as phones,
 * tablets, or larger devices.
 *
 * @param default The default composable to display when no specific size class conditions are met.
 * @param expanded The composable to display for large screen devices in landscape or desktop-like layouts.
 * @param portraitMulti The composable to display for portrait multi-window configurations (default for portrait modes).
 * @param portraitTablet (Optional) The composable to display for portrait tablet configurations.
 * @param portraitPhone (Optional) The composable to display for portrait phone configurations.
 */
@Composable
fun WindowSizeClass.Display(
    default: @Composable () -> Unit,
    expanded: @Composable () -> Unit,
    portraitMulti: @Composable () -> Unit,
    portraitTablet: (@Composable () -> Unit)? = null,
    portraitPhone: (@Composable () -> Unit)? = null,
    landscapePhone: (@Composable () -> Unit)? = null
) {
    val log = Logger.withTag("WindowSizeClass")

    val layout = remember(windowWidthSizeClass, windowHeightSizeClass) {
        when {
            isPortraitPhone() -> {
                log.v { "Layout: Portrait Phone" }
                portraitPhone ?: portraitMulti
            }

            isPortraitTablet() -> {
                log.v { "Layout: Portrait Tablet" }
                portraitTablet ?: portraitMulti
            }

            isIPadLandscape() -> {
                log.v { "Layout: iPad Landscape (Expanded-Medium)" }
                expanded
            }

            isLandscapePhone() -> {
                log.v { "Layout: Landscape Phone" }
                landscapePhone ?: expanded
            }

            isFullExpanded() -> {
                log.v { "Layout: Expanded (Full)" }
                expanded
            }

            else -> {
                log.v {
                    "Layout: Default - Width: $windowWidthSizeClass, Height: $windowHeightSizeClass"
                }
                default
            }
        }
    }

    layout()
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
