package com.oscarp.citiesapp.features.synccities

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun PreviewSyncContent_Loading_Saving() {
    SyncContent(
        state = SyncViewState(
            isLoading = true,
            percentSync = 65
        ),
        onLoad = {},
        onRetry = {}
    )
}

@Preview
@Composable
fun PreviewSyncContent_Error() {
    SyncContent(
        state = SyncViewState(
            isNoInternet = false,
            isError = true
        ),
        onLoad = {},
        onRetry = {}
    )
}

@Preview
@Composable
fun PreviewSyncContent_Completed() {
    SyncContent(
        state = SyncViewState(
            isCompleted = true
        ),
        onLoad = {},
        onRetry = {}
    )
}
