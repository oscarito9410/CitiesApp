package com.oscarp.citiesapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oscarp.citiesapp.synccities.SyncCitiesViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App(viewModel: SyncCitiesViewModel = koinInject()) {
    val syncProgressState = viewModel.syncProgress.collectAsState()

    MaterialTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = {
                viewModel.startSync()
            }) {
                Text("${syncProgressState.value}")
            }
        }
    }
}
