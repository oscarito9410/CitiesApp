package com.oscarp.citiesapp.synccities

sealed class SyncIntent {
    object StartSync : SyncIntent()
}
