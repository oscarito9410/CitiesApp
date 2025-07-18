package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.theme.Dimens

const val FavoriteButtonTag = "FavoriteButtonTag"
const val CityTextInfoTag = "CityTextInfoTag"
const val CityItemTag = "CityItemTag"

@Composable
fun CityItem(
    city: City,
    onCityClicked: () -> Unit,
    onToggleFavorite: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(CityItemTag + "_${city.id}")
            .fillMaxWidth()
            .clickable { onCityClicked() }
            .then(
                if (isSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.spacingXs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CityTextInfo(
                city,
                modifier = Modifier.weight(1f)
                    .testTag(CityTextInfoTag)
            )
            FavoriteButton(
                city,
                onToggleFavorite,
                modifier = Modifier.testTag(FavoriteButtonTag)
            )
        }
    }
}
