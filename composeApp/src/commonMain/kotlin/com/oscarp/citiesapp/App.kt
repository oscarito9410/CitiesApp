package com.oscarp.citiesapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.oscarp.citiesapp.navigation.AppNavigation
import com.oscarp.citiesapp.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }

    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            AppNavigation(
                snackbarHostState,
                contentPadding = paddingValues
            )
        }
    }
}
