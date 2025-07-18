package com.oscarp.citiesapp.features.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.ui.components.CityItem
import com.oscarp.citiesapp.ui.components.SearchFilterBar
import com.oscarp.citiesapp.ui.theme.Dimens
import com.oscarp.citiesapp.ui.utils.MultiWindowSizeLayout
import org.koin.compose.koinInject

const val RefreshLoadingIndicatorTag = "RefreshLoadingIndicator"
const val CitiesListTag = "CitiesList"

@Composable
fun CitiesScreen(
    viewModel: CitiesViewModel = koinInject(),
    onCityClicked: (City) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val cities = viewModel.paginatedCities.collectAsLazyPagingItems()

    MultiWindowSizeLayout(
        default = {
            SinglePaneCitiesScreen(
                searchQuery = state.searchQuery,
                showOnlyFavorites = state.showOnlyFavorites,
                onSearchQueryChanged = { query ->
                    viewModel.processIntent(
                        CitiesIntent.Search(query)
                    )
                },
                onToggleFavoritesFilter = {
                    viewModel.processIntent(
                        CitiesIntent.ToggleFavorite
                    )
                },
                cities = cities,
                onCityClicked = onCityClicked,
                onToggleFavorite = {
                } // implement if needed
            )
        },
        expanded = {
            TwoPaneCitiesScreen()
        },
        portraitTablet = {
            TwoPaneCitiesScreen()
        },
        landscapePhone = {
            TwoPaneCitiesScreen()
        }
    )
}

@Composable
fun SinglePaneCitiesScreen(
    searchQuery: String,
    showOnlyFavorites: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    cities: LazyPagingItems<City>,
    onCityClicked: (City) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchFilterBar(
            searchQuery = searchQuery,
            showOnlyFavorites = showOnlyFavorites,
            onSearchQueryChanged = onSearchQueryChanged,
            onToggleFavoritesFilter = onToggleFavoritesFilter,
            modifier = Modifier.padding(Dimens.spacingLarge)
        )

        CitiesList(
            cities = cities,
            onCityClicked = onCityClicked,
            onToggleFavorite = onToggleFavorite,
            selectedCity = null
        )
    }
}

@Composable
fun CitiesList(
    cities: LazyPagingItems<City>,
    onCityClicked: (City) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    selectedCity: City?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .testTag(CitiesListTag),
        contentPadding = PaddingValues(Dimens.spacingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
    ) {
        items(count = cities.itemCount, key = { "${it}_${cities[it]?.id}" }) { index ->
            val city = cities[index] ?: return@items
            CityItem(
                city = city,
                onCityClicked = { onCityClicked(city) },
                onToggleFavorite = { onToggleFavorite(city.id) },
                isSelected = selectedCity?.id == city.id
            )
        }
        citiesLoadState(cities)
    }
}

private fun LazyListScope.citiesLoadState(cities: LazyPagingItems<City>) {
    cities.loadState.apply {
        when {
            refresh is LoadStateNotLoading && cities.itemCount < 1 -> {
                item {
                    EmptyListState(modifier = Modifier.fillParentMaxSize())
                }
            }

            refresh is LoadStateLoading -> {
                item {
                    RefreshLoadingState(
                        modifier = Modifier.fillParentMaxSize()
                            .testTag(
                                RefreshLoadingIndicatorTag
                            )
                    )
                }
            }

            append is LoadStateLoading -> {
                item {
                    AppendLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spacingLarge),
                    )
                }
            }

            refresh is LoadStateError -> {
                val result = refresh as LoadStateError
                item {
                    ErrorListState(
                        errorMessage = result.error.message.orEmpty(),
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }

            append is LoadStateError -> {
                val result = append as LoadStateError
                item {
                    ErrorListState(
                        errorMessage = result.error.message.orEmpty(),
                        modifier = Modifier.fillParentMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun RefreshLoadingState(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AppendLoadingState(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmptyListState(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Items",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorListState(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TwoPaneCitiesScreen() {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Text("I'm a column single pane")
        }
        Box(modifier = Modifier.weight(1f)) {
            // Detail or info panel (optional)
            Text("Select a city")
        }
    }
}
