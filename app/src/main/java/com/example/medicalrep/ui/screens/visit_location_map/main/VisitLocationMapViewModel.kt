package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.common.location.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitLocationMapViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val locationHelper: LocationHelper,
) : BaseViewModel(appSettingsRepo) {

    private val route = savedStateHandle.toRoute<MainGraph.VisitLocationMap>()

    private val _state = MutableStateFlow(
        VisitLocationMapState(visitLocation = MapCameraTarget(route.visitLatitude, route.visitLongitude))
    )
    val state: StateFlow<VisitLocationMapState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val location = locationHelper.getLocation()
            _state.update { it.copy(userLocation = location) }
        }
    }

    fun onEvent(event: VisitLocationMapEvents) {
        when (event) {
            VisitLocationMapEvents.Back -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
