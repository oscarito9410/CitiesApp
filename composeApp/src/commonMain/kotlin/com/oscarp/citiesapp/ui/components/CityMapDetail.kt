package com.oscarp.citiesapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oscarp.citiesapp.navigation.CityMapDetail

@Composable
expect fun CityMapDetail(
    cityMapDetail: CityMapDetail,
    modifier: Modifier = Modifier
)
