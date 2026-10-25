package com.example.medicalrep.ui.screens.map_rep_tracking.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.MapRepTrackingFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapRepTrackingViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load the selected rep's visits and GPS track for the date range from Room / the backend.
    private val _state = MutableStateFlow(MapRepTrackingFakes.state())
    val state: StateFlow<MapRepTrackingState> = _state.asStateFlow()

    fun onEvent(event: MapRepTrackingEvents) {
        when (event) {
            MapRepTrackingEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is MapRepTrackingEvents.MarkerClicked -> _state.update { current ->
                if (current.visits.any { it.id == event.visitId }) {
                    current.copy(selectedVisitId = event.visitId)
                } else {
                    current
                }
            }
            is MapRepTrackingEvents.OpenVisitDetails ->
                sendEvent(UiEvent.Navigate(MainGraph.VisitDetails(visitId = event.visitId)))
        }
    }
}
