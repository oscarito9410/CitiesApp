package com.oscarp.citiesapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.action_clear_search
import citiesapp.composeapp.generated.resources.label_show_only_favorites
import citiesapp.composeapp.generated.resources.placeholder_search
import citiesapp.composeapp.generated.resources.search_icon_content_description
import citiesapp.composeapp.generated.resources.title_search_cities
import com.oscarp.citiesapp.ui.theme.Dimens
import org.jetbrains.compose.resources.stringResource

const val SearchTextFieldTag = "SearchTextFieldTag"
const val ClearSearchButtonTag = "ClearSearchButtonTag"
const val SearchFavoritesSwitchTag = "SearchFavoritesSwitchTag"
const val SearchFavoritesLabelTag = "SearchFavoritesLabelTag"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBar(
    searchQuery: String,
    showOnlyFavorites: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onShowFavoritesFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            singleLine = true,
            label = { Text(stringResource(Res.string.title_search_cities)) },
            placeholder = { Text(stringResource(Res.string.placeholder_search)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search_icon_content_description)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChanged("") },
                        modifier = Modifier.testTag(ClearSearchButtonTag)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.action_clear_search)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(SearchTextFieldTag)
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.label_show_only_favorites),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(SearchFavoritesLabelTag)
            )
            Switch(
                checked = showOnlyFavorites,
                onCheckedChange = { onShowFavoritesFilter() },
                modifier = Modifier.testTag(SearchFavoritesSwitchTag)
            )
        }
    }
}
