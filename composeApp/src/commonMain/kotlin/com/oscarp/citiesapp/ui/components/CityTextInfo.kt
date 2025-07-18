package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.theme.Dimens

@Composable
fun CityTextInfo(
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
