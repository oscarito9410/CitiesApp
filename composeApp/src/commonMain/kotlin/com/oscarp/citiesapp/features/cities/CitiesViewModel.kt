@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.oscarp.citiesapp.features.cities

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.oscarp.citiesapp.common.SharedViewModel
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.domain.usecases.GetPaginatedCitiesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CitiesViewModel(
    private val getPaginatedCitiesUseCase: GetPaginatedCitiesUseCase
) : SharedViewModel() {

    private val _state = MutableStateFlow(CitiesViewState())
    val state: StateFlow<CitiesViewState> = _state.asStateFlow()

    val paginatedCities: StateFlow<PagingData<City>> = state
        .debounce(DEBOUNCE_DELAY)
        .distinctUntilChanged()
        .flatMapLatest { state ->
            getPaginatedCitiesUseCase(
                searchQuery = state.searchQuery,
                onlyFavorites = state.showOnlyFavorites
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(0),
            PagingData.empty()
        )

    fun processIntent(intent: CitiesIntent) {
        when (intent) {
            is CitiesIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            CitiesIntent.ToggleFavorite -> {
                _state.update { it.copy(showOnlyFavorites = !it.showOnlyFavorites) }
            }
        }
    }

    companion object {
        private const val DEBOUNCE_DELAY = 300L
    }
}
