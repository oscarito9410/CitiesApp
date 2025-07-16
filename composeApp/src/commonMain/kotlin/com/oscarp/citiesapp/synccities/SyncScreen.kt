package com.oscarp.citiesapp.synccities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.text_error_sync_cities
import citiesapp.composeapp.generated.resources.text_sync_completed
import citiesapp.composeapp.generated.resources.text_sync_getting_cities
import citiesapp.composeapp.generated.resources.text_sync_retry
import citiesapp.composeapp.generated.resources.text_sync_saving_cities
import com.oscarp.citiesapp.components.AnimatedPercentText
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun SyncScreen(viewModel: SyncCitiesViewModel = koinInject()) {
    val viewState by viewModel.state.collectAsState()
    SyncContent(
        state = viewState,
        onLoad = {
            viewModel.processIntent(SyncIntent.StartSync)
        },
        onRetry = {
            viewModel.processIntent(SyncIntent.StartSync)
        }
    )
}

@Composable
fun SyncContent(
    state: SyncViewState,
    onLoad: () -> Unit,
    onRetry: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoad()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> LoadingContent(state.percentSync)
            state.isNoInternet || state.isError -> NoInternetContent(onRetry)
            state.isCompleted -> CompletedContent()
            else -> Unit
        }
    }
}

@Composable
private fun LoadingContent(percent: Int) {
    val animFromJsonRes by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/world.json").decodeToString()
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberLottiePainter(
                composition = animFromJsonRes,
                iterations = Compottie.IterateForever
            ),
            contentDescription = "Lottie animation"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (percent == 0) {
            Text(
                text = stringResource(Res.string.text_sync_getting_cities),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = stringResource(Res.string.text_sync_saving_cities),
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedPercentText(percent = percent)
        }
    }
}

@Composable
private fun NoInternetContent(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(Res.string.text_error_sync_cities),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(
                stringResource(Res.string.text_sync_retry)
            )
        }
    }
}

@Composable
private fun CompletedContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(Res.string.text_sync_completed),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun PreviewSyncScreen() {
    SyncContent(
        state = SyncViewState(isLoading = true, percentSync = 80),
        onRetry = {},
        onLoad = {}
    )
}
