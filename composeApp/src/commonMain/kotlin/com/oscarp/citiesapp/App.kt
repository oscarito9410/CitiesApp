package com.oscarp.citiesapp

import androidx.compose.runtime.Composable
import com.oscarp.citiesapp.synccities.SyncScreen
import com.oscarp.citiesapp.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        SyncScreen()
    }
}
