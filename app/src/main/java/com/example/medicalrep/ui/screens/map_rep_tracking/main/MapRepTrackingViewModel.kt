package com.example.medicalrep.ui.screens.map_rep_tracking.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.RoutePoint
import com.example.medicalrep.domain.repo.RouteRepo
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.map_rep_tracking.utils.MapRepTrackingFakes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MapRepTrackingViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val routeRepo: RouteRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load the selected rep's visits and GPS track for the date range from Room / the backend.
    private val _state = MutableStateFlow(MapRepTrackingFakes.state())
    val state: StateFlow<MapRepTrackingState> = _state.asStateFlow()

    init {
        loadRoute()
    }

    /** Road route through every GPS point of the rep's track, in order. */
    private fun loadRoute() {
        val points = _state.value.routePoints
        if (points.size < 2) return
        routeRepo.getRoute(points.map { RoutePoint(it.latitude, it.longitude) })
            .onEach { route ->
                _state.update { it.copy(route = route) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MapRepTrackingEvents) {
        when (event) {
            MapRepTrackingEvents.Back -> sendEvent(UiEvent.NavigationUp)
            MapRepTrackingEvents.RetryRoute -> loadRoute()
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
