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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import app.cash.paging.compose.itemKey
import citiesapp.composeapp.generated.resources.Res
import citiesapp.composeapp.generated.resources.text_empty_state_no_cities_found
import citiesapp.composeapp.generated.resources.text_empty_state_select_city
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.mappers.toCityMapDetail
import com.oscarp.citiesapp.ui.components.CityItem
import com.oscarp.citiesapp.ui.components.CityMapDetail
import com.oscarp.citiesapp.ui.components.SearchFilterBar
import com.oscarp.citiesapp.ui.theme.Dimens
import com.oscarp.citiesapp.ui.utils.DeviceLayoutMode
import com.oscarp.citiesapp.ui.utils.MultiWindowSizeLayout
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

const val RefreshLoadingIndicatorTag = "RefreshLoadingIndicator"
const val AppendLoadingIndicatorTag = "AppendLoadingIndicator"
const val CitiesListTag = "CitiesList"
const val CityMapDetailTag = "CityMapDetail"
const val SingleColumnCitiesListTag = "SingleColumnCitiesList"
const val EmptyCitiesListTag = "EmptyCitiesList"
const val EmptyCitySelectedTag = "EmptyCitySelected"

@Composable
fun CitiesScreen(
    viewModel: CitiesViewModel = koinInject(),
    hostState: SnackbarHostState? = null,
    onCityDetailNavigation: (City) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val cities = viewModel.paginatedCities.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        observeUiEffects(
            viewModel,
            cities,
            hostState,
            onCityDetailNavigation
        )
    }

    val onSearchQueryChanged: (String) -> Unit = {
        viewModel.processIntent(CitiesIntent.OnSearchQueryChanged(it))
    }
    val onShowFavoritesFilter: () -> Unit = {
        viewModel.processIntent(CitiesIntent.OnShowFavoritesFilter)
    }
    val onToggleFavorite: (City) -> Unit = {
        viewModel.processIntent(CitiesIntent.OnFavoriteToggled(it))
    }

    MultiWindowSizeLayout { layoutMode ->
        val isSinglePane = layoutMode == DeviceLayoutMode.SINGLE_PANE
        val onCitySelected: (City) -> Unit =
            {
                viewModel.processIntent(
                    CitiesIntent.OnCitySelected(
                        it,
                        isSinglePane
                    )
                )
            }

        if (isSinglePane) {
            SinglePaneCitiesScreenContent(
                selectedCity = state.selectedCity,
                searchQuery = state.searchQuery,
                showOnlyFavorites = state.showOnlyFavorites,
                onSearchQueryChanged = onSearchQueryChanged,
                onShowFavoritesFilter = onShowFavoritesFilter,
                cities = cities,
                onCityClicked = onCitySelected,
                onToggleFavorite = onToggleFavorite
            )
        } else {
            TwoPaneCitiesScreenContent(
                selectedCity = state.selectedCity,
                searchQuery = state.searchQuery,
                showOnlyFavorites = state.showOnlyFavorites,
                onSearchQueryChanged = onSearchQueryChanged,
                onShowFavoritesFilter = onShowFavoritesFilter,
                cities = cities,
                onCityClicked = onCitySelected,
                onToggleFavorite = onToggleFavorite
            )
        }
    }
}

private suspend fun observeUiEffects(
    viewModel: CitiesViewModel,
    cities: LazyPagingItems<City>,
    hostState: SnackbarHostState?,
    onCityDetailNavigation: (City) -> Unit
) {
    viewModel.uiEffect.collect { effect ->
        when (effect) {
            is CitiesEffect.ShowSnackBar -> {
                with(effect.localizedMessage) {
                    hostState?.showSnackbar(getString(resource))
                }
            }

            is CitiesEffect.RefreshCitiesPagination -> {
                cities.refresh()
            }

            is CitiesEffect.NavigateToCityDetails -> {
                onCityDetailNavigation(effect.city)
            }

            else -> Unit
        }
    }
}

@Composable
fun SinglePaneCitiesScreenContent(
    selectedCity: City?,
    searchQuery: String,
    showOnlyFavorites: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onShowFavoritesFilter: () -> Unit,
    cities: LazyPagingItems<City>,
    onCityClicked: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
            .testTag(SingleColumnCitiesListTag)
    ) {
        SearchFilterBar(
            searchQuery = searchQuery,
            showOnlyFavorites = showOnlyFavorites,
            onSearchQueryChanged = onSearchQueryChanged,
            onShowFavoritesFilter = onShowFavoritesFilter,
            modifier = Modifier.padding(Dimens.spacingLarge)
        )

        CitiesList(
            cities = cities,
            onCityClicked = onCityClicked,
            onToggleFavorite = onToggleFavorite,
            selectedCity = selectedCity
        )
    }
}

@Composable
fun TwoPaneCitiesScreenContent(
    selectedCity: City? = null,
    searchQuery: String,
    showOnlyFavorites: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onShowFavoritesFilter: () -> Unit,
    cities: LazyPagingItems<City>,
    onCityClicked: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            SearchFilterBar(
                searchQuery = searchQuery,
                showOnlyFavorites = showOnlyFavorites,
                onSearchQueryChanged = onSearchQueryChanged,
                onShowFavoritesFilter = onShowFavoritesFilter,
                modifier = Modifier.padding(Dimens.spacingLarge)
            )

            CitiesList(
                cities = cities,
                onCityClicked = onCityClicked,
                onToggleFavorite = onToggleFavorite,
                selectedCity = selectedCity
            )
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            selectedCity?.let {
                CityMapDetail(
                    cityMapDetail = it.toCityMapDetail(),
                    modifier = Modifier.fillMaxSize()
                        .testTag(CityMapDetailTag)
                )
            } ?: EmptyListState(
                text = stringResource(Res.string.text_empty_state_select_city),
                modifier = Modifier.fillMaxSize()
                    .testTag(EmptyCitySelectedTag)
            )
        }
    }
}

@Composable
fun CitiesList(
    cities: LazyPagingItems<City>,
    onCityClicked: (City) -> Unit,
    onToggleFavorite: (City) -> Unit,
    selectedCity: City?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .testTag(CitiesListTag),
        contentPadding = PaddingValues(Dimens.spacingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
    ) {
        items(
            count = cities.itemCount,
            key = cities.itemKey { city -> city.id }
        ) { index ->
            val city = cities[index] ?: return@items
            CityItem(
                city = city,
                onCityClicked = { onCityClicked(city) },
                onToggleFavorite = { onToggleFavorite(city) },
                isSelected = selectedCity?.id == city.id,
                modifier = Modifier.animateItem()
            )
        }
        citiesLoadState(cities)
    }
}

private fun LazyListScope.citiesLoadState(
    cities: LazyPagingItems<City>
) {
    cities.loadState.apply {
        val isRefreshError = refresh is LoadStateError
        val isEmptyListAndNotLoadingOrError =
            refresh is LoadStateNotLoading && append is LoadStateNotLoading &&
                cities.itemCount == 0 &&
                !isRefreshError

        when {
            refresh is LoadStateLoading -> {
                item {
                    RefreshLoadingState(
                        modifier = Modifier.fillParentMaxSize()
                            .testTag(RefreshLoadingIndicatorTag)
                    )
                }
            }

            append is LoadStateLoading -> {
                item {
                    AppendLoadingState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(AppendLoadingIndicatorTag)
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

            isEmptyListAndNotLoadingOrError -> {
                item {
                    EmptyListState(
                        text = stringResource(Res.string.text_empty_state_no_cities_found),
                        modifier = Modifier.fillMaxSize()
                            .testTag(EmptyCitiesListTag)
                            .animateItem()
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
fun EmptyListState(
    text: String = "",
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
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
