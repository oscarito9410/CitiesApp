@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.oscarp.citiesapp.features.cities

import androidx.paging.PagingData
import com.oscarp.citiesapp.common.SharedViewModel
import com.oscarp.citiesapp.domain.exceptions.CityNotFoundException
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase
import com.oscarp.citiesapp.domain.usecases.ToggleFavoriteUseCase
import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CitiesViewModel(
    private val getPaginatedCitiesUseCase: GetPaginatedCitiesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : SharedViewModel() {

    private val _state = MutableStateFlow(CitiesViewState())
    val state: StateFlow<CitiesViewState> = _state.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CitiesEffect>()
    val uiEffect: SharedFlow<CitiesEffect> = _uiEffect.asSharedFlow()

    val paginatedCities: StateFlow<PagingData<City>> = state
        .debounce(DEBOUNCE_DELAY)
        .distinctUntilChanged()
        .flatMapLatest { state ->
            getPaginatedCitiesUseCase(
                searchQuery = state.searchQuery,
                onlyFavorites = state.showOnlyFavorites,
                cachedIn = viewModelScope
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PagingData.empty()
        )

    fun processIntent(intent: CitiesIntent) {
        when (intent) {
            is CitiesIntent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is CitiesIntent.OnFavoriteToggled -> {
                handleToggleFavorite(intent.city)
            }

            is CitiesIntent.OnCitySelected -> {
                handleCitySelected(intent.city, intent.isSinglePane)
            }

            CitiesIntent.OnShowFavoritesFilter -> {
                _state.update { it.copy(showOnlyFavorites = !it.showOnlyFavorites) }
            }
        }
    }

    private fun handleCitySelected(
        city: City,
        isSinglePane: Boolean
    ) {
        if (isSinglePane) {
            _state.update { it.copy(selectedCity = city) }
        } else {
            viewModelScope.launch {
                _uiEffect.emit(CitiesEffect.NavigateToCityDetails(city))
            }
        }
    }

    private fun handleToggleFavorite(city: City) {
        viewModelScope.launch {
            val cityId = city.id
            val newFavoriteStatus = !city.isFavorite
            try {
                val successfullyUpdated = toggleFavoriteUseCase(cityId)

                if (successfullyUpdated) {
                    if (_state.value.showOnlyFavorites && !newFavoriteStatus) {
                        _uiEffect.emit(CitiesEffect.RefreshCitiesPagination)
                    }
                } else {
                    _uiEffect.emit(
                        CitiesEffect.ShowSnackBar(
                            LocalizedMessage.FailedToUpdateFavoriteStatus
                        )
                    )
                }
            } catch (_: CityNotFoundException) {
                _uiEffect.emit(
                    CitiesEffect.ShowSnackBar(
                        LocalizedMessage.CityNotFound
                    )
                )
            }
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY = 300L
    }
}
