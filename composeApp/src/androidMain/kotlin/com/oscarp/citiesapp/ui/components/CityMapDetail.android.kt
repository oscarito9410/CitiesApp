package com.oscarp.citiesapp.ui.components

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
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
    city: City,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
    ) {
        val isInPreview = LocalInspectionMode.current
        val isInTest = Build.FINGERPRINT == "robolectric"

        if (isInPreview || isInTest) {
            Text(
                "Map render in test",
                modifier = modifier
            )
            return
        }

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
