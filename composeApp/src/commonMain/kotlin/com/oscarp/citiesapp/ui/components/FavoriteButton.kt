package com.oscarp.citiesapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.button_text_add_to_favorites
import citiesapp.composeapp.generated.resources.button_text_remove_from_favorites
import com.oscarp.citiesapp.domain.models.City
import org.jetbrains.compose.resources.stringResource

const val FavoriteIconTag = "FavoriteIcon"

@Composable
fun FavoriteButton(
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
            stringResource(Res.string.button_text_remove_from_favorites)
        } else {
            stringResource(Res.string.button_text_add_to_favorites)
        }
        val tint = if (isFavorite) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

        Icon(
            imageVector = icon,
            modifier = Modifier.testTag(FavoriteIconTag),
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
