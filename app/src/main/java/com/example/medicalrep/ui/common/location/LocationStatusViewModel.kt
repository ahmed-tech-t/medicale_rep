package com.example.medicalrep.ui.common.location

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.ui.utils.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LocationStatusEvents {
    data object Refresh : LocationStatusEvents
}

@HiltViewModel
class LocationStatusViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val locationHelper: LocationHelper,
) : BaseViewModel(appSettingsRepo) {

    private val _status = MutableStateFlow(locationHelper.status())
    val status: StateFlow<LocationStatus> = _status.asStateFlow()

    init {
        viewModelScope.launch {
            locationHelper.statusChanges().collect { _status.value = locationHelper.status() }
        }
    }

    fun onEvent(event: LocationStatusEvents) {
        when (event) {
            LocationStatusEvents.Refresh -> _status.value = locationHelper.status()
        }
    }
}
