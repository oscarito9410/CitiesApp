package com.oscarp.citiesapp.features.mapdetail

import androidx.compose.runtime.Composable
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.mappers.toCityMapDetail
import com.oscarp.citiesapp.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val fakeCity = City(
    id = 1L,
    name = "Preview City",
    latitude = 10.0,
    longitude = 10.0,
    isFavorite = true,
    country = "MX"
)

@Composable
@Preview
fun PreviewMapDetailScreen() {
    AppTheme {
        MapDetailScreen(
            cityMapDetail = fakeCity.toCityMapDetail(),
            onBack = {}
        )
    }
}
