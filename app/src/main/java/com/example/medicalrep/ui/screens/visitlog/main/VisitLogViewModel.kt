package com.example.medicalrep.ui.screens.visitlog.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.medicalrep.domain.repo.DraftVisitRepo
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitLogViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val draftVisitRepo: DraftVisitRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load required / done visits from Room.
    private val _state = MutableStateFlow(VisitLogFakes.state())
    val state: StateFlow<VisitLogState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            draftVisitRepo.draft().collect { draft -> _state.update { it.copy(hasPendingVisit = draft != null) } }
        }
    }

    fun onEvent(event: VisitLogEvents) {
        when (event) {
            VisitLogEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is VisitLogEvents.SelectTab -> _state.update {
                it.copy(selectedTab = event.tab, filter = VisitFilter.All, overlay = VisitLogOverlay.None)
            }
            is VisitLogEvents.StartVisit ->
                if (_state.value.hasPendingVisit) _state.update { it.copy(overlay = VisitLogOverlay.PendingVisit(event.visitId)) }
                else sendEvent(UiEvent.Navigate(MainGraph.PlanVisit(visitId = event.visitId)))

            VisitLogEvents.ConfirmPending -> confirmPending()
            is VisitLogEvents.OpenVisit -> sendEvent(UiEvent.Navigate(MainGraph.VisitDetails(visitId = event.visitId)))
            VisitLogEvents.OpenFilter -> _state.update { it.copy(overlay = VisitLogOverlay.Filter) }
            VisitLogEvents.DismissOverlay -> _state.update { it.copy(overlay = VisitLogOverlay.None) }
            is VisitLogEvents.SelectFilter -> _state.update {
                it.copy(filter = event.filter, overlay = VisitLogOverlay.None)
            }
        }
    }

    private fun confirmPending() {
        val pending = _state.value.overlay as? VisitLogOverlay.PendingVisit ?: return
        viewModelScope.launch {
            draftVisitRepo.discard()
            _state.update { it.copy(overlay = VisitLogOverlay.None) }
            sendEvent(UiEvent.Navigate(MainGraph.PlanVisit(visitId = pending.visitId)))
        }
    }
}
