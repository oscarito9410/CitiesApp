package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.oscarp.citiesapp.domain.models.City

const val ZoomKms = 50.0f

@Composable
actual fun CityMapDetail(
    city: City
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val coordinates = LatLng(
            city.latitude,
            city.longitude
        )

        val markerState = rememberUpdatedMarkerState(position = coordinates)
        val cameraPositionState = rememberCameraPositionState()

        LaunchedEffect(coordinates) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(coordinates, ZoomKms),
            )
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                title = city.name,
                snippet = city.name
            )
        }
    }
}
