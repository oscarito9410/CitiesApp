package com.oscarp.citiesapp.features.mapdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oscarp.citiesapp.navigation.CityMapDetail
import com.oscarp.citiesapp.ui.components.CityMapDetail

const val BackContentDescription = "Back"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapDetailScreen(
    cityMapDetail: CityMapDetail,
    onBack: () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(cityMapDetail.name) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = BackContentDescription
                    )
                }
            }
        )
        CityMapDetail(
            cityMapDetail,
            modifier = Modifier.fillMaxSize()
        )
    }
}
