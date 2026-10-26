package com.example.medicalrep.ui.screens.visitplanning.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.visitplanning.utils.VisitPlanningFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class VisitPlanningViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load pending / planned visits from Room and send the visit request to the backend.
    private val _state = MutableStateFlow(VisitPlanningFakes.state())
    val state: StateFlow<VisitPlanningState> = _state.asStateFlow()

    fun onEvent(event: VisitPlanningEvents) {
        when (event) {
            VisitPlanningEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is VisitPlanningEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
            VisitPlanningEvents.OpenFilter -> _state.update { it.copy(overlay = VisitPlanningOverlay.Filter) }
            is VisitPlanningEvents.SelectFilter -> _state.update {
                it.copy(filter = event.filter, overlay = VisitPlanningOverlay.None)
            }
            is VisitPlanningEvents.ToggleSelect -> _state.update { it.copy(selectedIds = it.selectedIds.toggle(event.visitId)) }
            VisitPlanningEvents.OpenSchedule -> _state.update { it.copy(overlay = VisitPlanningOverlay.ScheduleVisit) }
            VisitPlanningEvents.DismissOverlay -> _state.update { it.copy(overlay = VisitPlanningOverlay.None) }
            is VisitPlanningEvents.DateChanged -> _state.update { it.copy(scheduleDateMillis = event.millis) }
            VisitPlanningEvents.RequestVisit -> {
                _state.update { it.copy(selectedIds = emptySet(), overlay = VisitPlanningOverlay.None) }
                showComingSoon()
            }
        }
    }

    private fun showComingSoon() {
        sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.placeholder_coming_soon)))
    }

    private fun Set<String>.toggle(id: String): Set<String> = if (id in this) this - id else this + id
}
