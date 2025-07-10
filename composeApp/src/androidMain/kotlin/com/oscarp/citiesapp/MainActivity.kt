package com.oscarp.citiesapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    val syncUseCase: SyncCitiesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }

        lifecycleScope.launch {
            var startTime = 0L

            syncUseCase()
                .onStart {
                    startTime = System.currentTimeMillis()
                    Log.d("syncUseCase", "Sync started at $startTime")
                }
                .onCompletion { cause ->
                    val endTime = System.currentTimeMillis()
                    val duration = endTime - startTime
                    if (cause == null) {
                        Log.d("syncUseCase", "Sync completed in ${TimeUnit.MILLISECONDS.toSeconds(duration)}")
                    } else {
                        Log.d("syncUseCase", "Sync failed after $duration ms: $cause")
                    }
                }
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { progress ->
                    Log.d("syncUseCase", "$progress")
                }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
