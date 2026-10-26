package com.example.medicalrep.ui.screens.reptracking.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.reptracking.utils.RepTrackingFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RepTrackingViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load the reps and the selected rep's visits for the selected day from Room / the backend.
    private val _state = MutableStateFlow(RepTrackingFakes.state())
    val state: StateFlow<RepTrackingState> = _state.asStateFlow()

    fun onEvent(event: RepTrackingEvents) {
        when (event) {
            RepTrackingEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is RepTrackingEvents.SelectRep -> if (event.name.isNotBlank()) {
                _state.update { it.copy(selectedRep = event.name) }
            }
            is RepTrackingEvents.DateChanged -> _state.update { it.copy(dateMillis = event.millis) }
            is RepTrackingEvents.OpenVisit ->
                sendEvent(UiEvent.Navigate(MainGraph.VisitDetails(visitId = event.visitId)))
            RepTrackingEvents.ViewRoute -> sendEvent(UiEvent.Navigate(MainGraph.MapRepTracking))
        }
    }
}
