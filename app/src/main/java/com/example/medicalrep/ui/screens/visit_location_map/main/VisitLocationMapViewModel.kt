package com.example.medicalrep.ui.screens.visit_location_map.main

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.visit_location_map.utils.VisitLocationMapFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VisitLocationMapViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(appSettingsRepo) {

    private val route = savedStateHandle.toRoute<MainGraph.VisitLocationMap>()

    // TODO: replace the fake with the device's real location (fused location provider + permission).
    private val _state = MutableStateFlow(
        VisitLocationMapState(
            userLocation = VisitLocationMapFakes.userLocation,
            visitLocation = MapCameraTarget(route.visitLatitude, route.visitLongitude),
        )
    )
    val state: StateFlow<VisitLocationMapState> = _state.asStateFlow()

    fun onEvent(event: VisitLocationMapEvents) {
        when (event) {
            VisitLocationMapEvents.Back -> sendEvent(UiEvent.NavigationUp)
        }
    }
}
