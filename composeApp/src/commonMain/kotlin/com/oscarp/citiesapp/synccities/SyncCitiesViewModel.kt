package com.oscarp.citiesapp.synccities

import com.oscarp.citiesapp.common.SharedViewModel
import com.oscarp.citiesapp.domain.models.SyncProgress
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncCitiesViewModel(
    private val syncCitiesUseCase: SyncCitiesUseCase
) : SharedViewModel() {

    private val _syncProgress = MutableStateFlow<SyncProgress>(SyncProgress.Started)
    val syncProgress: StateFlow<SyncProgress> = _syncProgress
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            SyncProgress.Started
        )

    fun startSync() {
        viewModelScope.launch {
            syncCitiesUseCase()
                .catch { _syncProgress.value = SyncProgress.Error(it) }
                .onCompletion {
                    _syncProgress.value = SyncProgress.Completed
                }
                .collect {
                    _syncProgress.value = it
                }
        }
    }
}
