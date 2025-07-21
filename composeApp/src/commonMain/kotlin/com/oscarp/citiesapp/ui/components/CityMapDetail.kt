package com.oscarp.citiesapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oscarp.citiesapp.domain.models.City

@Composable
expect fun CityMapDetail(
    city: City,
    modifier: Modifier = Modifier
)
