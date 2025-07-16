package com.oscarp.citiesapp.features.synccities

sealed class SyncIntent {
    object StartSync : SyncIntent()
}
