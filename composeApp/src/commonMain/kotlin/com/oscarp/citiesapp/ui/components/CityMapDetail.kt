package com.oscarp.citiesapp.ui.components

import androidx.compose.runtime.Composable
import com.oscarp.citiesapp.domain.models.City

@Composable
expect fun CityMapDetail(
    city: City
)
