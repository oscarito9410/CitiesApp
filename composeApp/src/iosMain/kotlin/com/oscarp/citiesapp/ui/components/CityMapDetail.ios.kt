package com.oscarp.citiesapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.oscarp.citiesapp.navigation.CityMapDetail
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
    cityMapDetail: CityMapDetail,
    modifier: Modifier
) {
    UIKitView(
        factory = {
            MKMapView()
        },
        modifier = modifier,
        update = { mapView ->
            // this update block is called whenever the composable recomposes.

            val coordinates = CLLocationCoordinate2DMake(cityMapDetail.latitude, cityMapDetail.longitude)

            val region = MKCoordinateRegionMakeWithDistance(
                centerCoordinate = coordinates,
                latitudinalMeters = ZoomKms,
                longitudinalMeters = ZoomKms
            )
            mapView.setRegion(region, animated = true)

            val annotation = MKPointAnnotation().apply {
                setCoordinate(coordinates)
                setTitle(cityMapDetail.name)
                setSubtitle(cityMapDetail.countryCode)
            }

            mapView.removeAnnotations(mapView.annotations)
            mapView.addAnnotation(annotation)
        }
    )
}
