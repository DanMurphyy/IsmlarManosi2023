package com.hfad.ismlarmanosi2023

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.ismlarmanosi2023.remote.Resource
import com.hfad.ismlarmanosi2023.remote.domain.GetRemoteConfigUseCase
import com.hfad.ismlarmanosi2023.remote.model.RemoteConfigModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getRemoteConfigUseCase: GetRemoteConfigUseCase) :
    ViewModel() {
    private val _remoteConfig = MutableStateFlow<Resource<RemoteConfigModel>?>(null)
    var remoteConfig: StateFlow<Resource<RemoteConfigModel>?> = _remoteConfig

    fun getRemoteConfig(id: String) {
        viewModelScope.launch {
            getRemoteConfigUseCase.invoke(id).collectLatest { result ->
                _remoteConfig.value = result
            }
        }
    }
}