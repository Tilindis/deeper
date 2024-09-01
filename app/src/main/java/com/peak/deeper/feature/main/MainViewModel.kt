package com.peak.deeper.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peak.deeper.utils.datastore.DataStore
import com.peak.deeper.utils.interactor.MainInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainInteractor: MainInteractor,
    private val dataStore: DataStore,
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            dataStore.getUserIdValue().collect { userId ->
                userId?.let {
                    mainInteractor.getScansByUserId(it).collect { scans ->
                        _state.update { state ->
                            state.copy(scans = scans.map { scan -> scan.toScanViewData() })
                        }
                    }
                }
            }
        }
    }
}