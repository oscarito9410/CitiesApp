package com.oscarp.citiesapp

import androidx.compose.runtime.Composable
import com.oscarp.citiesapp.features.synccities.SyncScreen
import com.oscarp.citiesapp.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        SyncScreen()
    }
}
