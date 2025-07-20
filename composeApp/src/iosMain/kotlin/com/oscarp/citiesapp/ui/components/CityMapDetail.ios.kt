package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.oscarp.citiesapp.domain.models.City
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

// 5km zoom
const val ZoomKms = 5000.0

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CityMapDetail(
    city: City
) {
    UIKitView(
        factory = {
            MKMapView()
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            // this update block is called whenever the composable recomposes.

            val coordinates = CLLocationCoordinate2DMake(city.latitude, city.longitude)

            val region = MKCoordinateRegionMakeWithDistance(
                centerCoordinate = coordinates,
                latitudinalMeters = ZoomKms,
                longitudinalMeters = ZoomKms
            )
            mapView.setRegion(region, animated = true)

            val annotation = MKPointAnnotation().apply {
                setCoordinate(coordinates)
                setTitle(city.name)
                setSubtitle(city.country)
            }

            mapView.removeAnnotations(mapView.annotations)
            mapView.addAnnotation(annotation)
        }
    )
}
