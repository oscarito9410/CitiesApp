package com.oscarp.citiesapp.synccities

import com.oscarp.citiesapp.common.SharedViewModel
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SyncCitiesViewModel(
    private val useCase: SyncCitiesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : SharedViewModel() {

    private val _state = MutableStateFlow<SyncViewState>(SyncViewState.Idle)
    val state: StateFlow<SyncViewState> = _state

    fun processIntent(intent: SyncIntent) {
        when (intent) {
            SyncIntent.StartSync -> startSync()
        }
    }

    private fun startSync() {
        viewModelScope.launch(ioDispatcher) {
            useCase()
                .onStart { _state.value = SyncViewState.Loading }
                .map {
                    SyncViewState.Inserting(it)
                }
                .onCompletion { _state.value = SyncViewState.Completed }
                .catch { e ->
                    _state.value = SyncViewState.Error(e)
                }
                .collect { _state.value = it }
        }
    }
}
