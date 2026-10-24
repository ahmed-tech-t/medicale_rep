package com.example.medicalrep.ui.screens.visitlog.main

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.visitlog.utils.VisitLogFakes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class VisitLogViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
) : BaseViewModel(appSettingsRepo) {

    // TODO: load required / done visits from Room.
    private val _state = MutableStateFlow(VisitLogFakes.state())
    val state: StateFlow<VisitLogState> = _state.asStateFlow()

    fun onEvent(event: VisitLogEvents) {
        when (event) {
            VisitLogEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is VisitLogEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
            is VisitLogEvents.StartVisit -> sendEvent(UiEvent.Navigate(MainGraph.PlanVisit(visitId = event.visitId)))
            VisitLogEvents.FilterClick ->
                sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.placeholder_coming_soon)))
        }
    }
}
