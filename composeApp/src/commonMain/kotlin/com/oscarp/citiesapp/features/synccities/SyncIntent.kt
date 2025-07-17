package com.oscarp.citiesapp.features.synccities

sealed class SyncIntent {
    object VerifyLoadSync : SyncIntent()
    object StartSync : SyncIntent()
}
