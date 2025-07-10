package com.oscarp.citiesapp.common

import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class SharedViewModel : AndroidXViewModel() {

    actual val viewModelScope: CoroutineScope get() = androidXViewModelScope

    actual override fun onCleared(): Unit = super.onCleared()
}
