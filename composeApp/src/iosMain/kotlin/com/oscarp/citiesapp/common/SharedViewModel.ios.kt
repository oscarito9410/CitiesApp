package com.oscarp.citiesapp.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual open class SharedViewModel {

    actual val viewModelScope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main)

    protected actual open fun onCleared() {
        viewModelScope.cancel()
    }
}
