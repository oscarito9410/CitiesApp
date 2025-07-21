package com.oscarp.citiesapp.features.cities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.theme.AppTheme
import kotlinx.coroutines.flow.flowOf
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
private fun fakePagingItems(): LazyPagingItems<City> {
    return flowOf(
        PagingData.from(
            listOf(fakeCity),
            sourceLoadStates =
            app.cash.paging.LoadStates(
                refresh = LoadStateNotLoading(false),
                append = LoadStateNotLoading(false),
                prepend = LoadStateNotLoading(false),
            ),
        ),
    ).collectAsLazyPagingItems()
}

@Preview
@Composable
fun PreviewSinglePaneCitiesScreen() {
    AppTheme {
        SinglePaneCitiesScreenContent(
            selectedCity = null,
            searchQuery = "Preview",
            showOnlyFavorites = true,
            onSearchQueryChanged = {},
            onShowFavoritesFilter = {},
            cities = fakePagingItems(),
            onCityClicked = {},
            onToggleFavorite = {}
        )
    }
}

@Preview
@Composable
fun TwoPaneLandscapePreview() {
    AppTheme {
        TwoPaneCitiesScreenContent(
            selectedCity = fakeCity,
            searchQuery = "Preview",
            showOnlyFavorites = true,
            onSearchQueryChanged = {},
            onShowFavoritesFilter = {},
            cities = fakePagingItems(),
            onCityClicked = {},
            onToggleFavorite = {},
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
