package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.add_to_favorites
import citiesapp.composeapp.generated.resources.remove_from_favorites
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.theme.Dimens
import org.jetbrains.compose.resources.stringResource

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
            )
            FavoriteButton(city, onToggleFavorite)
        }
    }
}

@Composable
private fun CityTextInfo(
    city: City,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = city.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(Dimens.spacingXs))

        Text(
            text = city.coordinates,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FavoriteButton(
    city: City,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleFavorite,
        modifier = modifier
    ) {
        val isFavorite = city.isFavorite
        val icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
        val contentDescription = if (isFavorite) {
            stringResource(Res.string.remove_from_favorites)
        } else {
            stringResource(Res.string.add_to_favorites)
        }
        val tint = if (isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
