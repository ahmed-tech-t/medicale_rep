package com.example.medicalrep.ui.screens.map_rep_tracking.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.repo.RouteRepo
import com.example.medicalrep.domain.repo.TrackPointRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.map_rep_tracking.models.TrackUserMapMarker
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapRepTrackingViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    savedStateHandle: SavedStateHandle,
    private val routeRepo: RouteRepo,
    private val visitRepo: VisitRepo,
    private val trackPointRepo: TrackPointRepo,
) : BaseViewModel(appSettingsRepo) {

    // The route arguments are in the handle under their names; read by name, they can be set in a JVM test.
    private val repId: String = requireNotNull(savedStateHandle["repId"])
    private val dayMillis: Long = requireNotNull(savedStateHandle["dayMillis"])

    private val _state = MutableStateFlow(MapRepTrackingState())
    val state: StateFlow<MapRepTrackingState> = _state.asStateFlow()

    init {
        loadDay()
    }

    /** The rep's finished visits and GPS points of the day, read once when the map opens. */
    private fun loadDay() {
        val from = startOfDay(dayMillis)
        val to = startOfNextDay(dayMillis)
        viewModelScope.launch {
            val visits = visitRepo.doneOnDay(repId, from, to).first().map(Visit::toUi)
            // The database includes both ends of a range, so the last millisecond of the day is `to - 1`.
            val points = trackPointRepo.byRepAndDay(repId, from, to - 1).first().map {
                TrackUserMapMarker(it.id.toInt(), it.latitude, it.longitude, it.visitId.orEmpty(), it.timeMillis)
            }
            _state.update { it.copy(visits = visits, routePoints = points, selectedVisitId = visits.firstOrNull()?.id) }
            loadRoute()
        }
    }

    /** Road route through every GPS point of the rep's track, in order. */
    private fun loadRoute() {
        val points = _state.value.routePoints
        if (points.size < 2) return
        routeRepo.getRoute(points.map { GeoPoint(it.latitude, it.longitude) })
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
