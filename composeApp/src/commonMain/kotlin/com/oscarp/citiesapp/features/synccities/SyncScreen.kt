package com.oscarp.citiesapp.features.synccities

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.text_error_sync_cities
import citiesapp.composeapp.generated.resources.text_sync_completed
import citiesapp.composeapp.generated.resources.text_sync_getting_cities
import citiesapp.composeapp.generated.resources.text_sync_retry
import citiesapp.composeapp.generated.resources.text_sync_saving_cities
import com.oscarp.citiesapp.ui.components.AnimatedPercentText
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.stringResource

const val TagLoading = "LoadingContent"
const val TagRetryButton = "RetryButton"
const val TagNotInternet = "NoInternetContent"
const val TagCompleted = "CompletedContent"

@Composable
fun SyncScreen(
    viewModel: SyncCitiesViewModel,
    coordinator: SyncCitiesCoordinator,
) {
    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        coordinator.onScreenLoaded()
    }

    LaunchedEffect(viewState.isCompleted) {
        if (viewState.isCompleted) {
            coordinator.onSyncCompleted()
        }
    }

    SyncContent(
        state = viewState,
        onRetry = coordinator::onRetryClicked
    )
}

@Composable
fun SyncContent(
    state: SyncViewState,
    onRetry: () -> Unit
) {
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
        modifier = Modifier.fillMaxSize().testTag(TagLoading),
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
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = stringResource(Res.string.text_sync_saving_cities),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            AnimatedPercentText(
                percent = percent,
            )
        }
    }
}

@Composable
private fun NoInternetContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().testTag(TagNotInternet),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(Res.string.text_error_sync_cities),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(TagRetryButton)
        ) {
            Text(stringResource(Res.string.text_sync_retry))
        }
    }
}

@Composable
private fun CompletedContent() {
    Column(
        modifier = Modifier.fillMaxSize().testTag(TagCompleted),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val animFromJsonRes by rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/compass.json").decodeToString()
            )
        }
        Image(
            painter = rememberLottiePainter(
                composition = animFromJsonRes,
                iterations = Compottie.IterateForever
            ),
            contentDescription = "Lottie animation"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(Res.string.text_sync_completed),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}
