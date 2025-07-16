package com.oscarp.citiesapp.synccities

import com.oscarp.citiesapp.common.SharedViewModel
import com.oscarp.citiesapp.domain.models.CityDownload
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncCitiesViewModel(
    private val useCase: SyncCitiesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : SharedViewModel() {

    private val _state = MutableStateFlow(SyncViewState())
    val state: StateFlow<SyncViewState> = _state

    fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.StartSync -> startSync()
        }
    }

    private fun startSync() {
        viewModelScope.launch(ioDispatcher) {
            useCase()
                .onStart {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            error = exception
                        )
                    }
                }
                .onCompletion {
                    _state.update {
                        it.copy(
                            isCompleted = true,
                            isLoading = false
                        )
                    }
                }
                .collect { response ->
                    val percent = calculateProgress(response)
                    _state.update {
                        it.copy(
                            isLoading = true,
                            percentSync = percent
                        )
                    }
                }
        }
    }

    private fun calculateProgress(response: CityDownload) = with(response) {
        (totalInserted * PROGRESS_FACTOR / totalCities).coerceIn(
            0,
            PROGRESS_FACTOR
        )
    }

    companion object {
        private const val PROGRESS_FACTOR = 100
    }
}
