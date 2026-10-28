package com.example.medicalrep.ui.screens.reptracking.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.domain.repo.RepRepo
import com.example.medicalrep.domain.repo.StatisticsRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Whose day is shown. */
private data class Selection(val repId: String, val dayMillis: Long)

/** What the screen shows for one rep and one day. */
private data class DayLog(val visits: List<VisitUi> = emptyList(), val progress: VisitProgress = VisitProgress())

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RepTrackingViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    repRepo: RepRepo,
    private val visitRepo: VisitRepo,
    private val statisticsRepo: StatisticsRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(RepTrackingState(dateMillis = System.currentTimeMillis()))
    val state: StateFlow<RepTrackingState> = _state.asStateFlow()

    init {
        // The first rep is selected as soon as there is one.
        repRepo.byRole(RepRole.Rep)
            .onEach { reps ->
                _state.update { it.copy(reps = reps.map(Rep::toUi), selectedRepId = it.selectedRepId ?: reps.firstOrNull()?.id) }
            }
            .launchIn(viewModelScope)
        // A new rep or day replaces the visits and the progress; the old ones are cleared while the new ones load.
        _state.map { state -> state.selectedRepId?.let { Selection(it, state.dateMillis) } }
            .distinctUntilChanged()
            .flatMapLatest { selection -> if (selection == null) flowOf(DayLog()) else dayLog(selection) }
            .onEach { log -> _state.update { it.copy(visits = log.visits, progress = log.progress) } }
            .launchIn(viewModelScope)
    }

    private fun dayLog(selection: Selection): Flow<DayLog> {
        val from = startOfDay(selection.dayMillis)
        val to = startOfNextDay(selection.dayMillis)
        return combine(
            visitRepo.doneOnDay(selection.repId, from, to),
            statisticsRepo.progressOf(selection.repId, from, to),
        ) { visits, progress -> DayLog(visits.map(Visit::toUi), progress) }
            .onStart { emit(DayLog()) }
    }

    fun onEvent(event: RepTrackingEvents) {
        when (event) {
            RepTrackingEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is RepTrackingEvents.SelectRep -> if (_state.value.reps.any { it.id == event.repId }) {
                _state.update { it.copy(selectedRepId = event.repId) }
            }
            is RepTrackingEvents.DateChanged -> _state.update { it.copy(dateMillis = event.millis) }
            is RepTrackingEvents.OpenVisit ->
                sendEvent(UiEvent.Navigate(MainGraph.VisitDetails(visitId = event.visitId)))
            RepTrackingEvents.ViewRoute -> _state.value.selectedRepId?.let { repId ->
                sendEvent(UiEvent.Navigate(MainGraph.MapRepTracking(repId = repId, dayMillis = _state.value.dateMillis)))
            }
        }
    }
}
