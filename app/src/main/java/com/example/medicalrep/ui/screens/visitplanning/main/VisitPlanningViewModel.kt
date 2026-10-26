package com.example.medicalrep.ui.screens.visitplanning.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiText
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.R
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitplanning.utils.asPlanRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** The tab and filter that decide which visits the repo is asked for. */
private data class VisitPlanningQuery(val tab: VisitPlanningTab, val filter: VisitFilter)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VisitPlanningViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val visitRepo: VisitRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(VisitPlanningState(scheduleDateMillis = System.currentTimeMillis()))
    val state: StateFlow<VisitPlanningState> = _state.asStateFlow()

    private val query = _state.map { VisitPlanningQuery(it.selectedTab, it.filter) }.distinctUntilChanged()

    /** Visits tab: finished visits to plan again. Pending tab: plan requests waiting for (or refused by) the admin. */
    val visits: Flow<PagingData<VisitUi>> = query
        .flatMapLatest { (tab, filter) ->
            when (tab) {
                VisitPlanningTab.Visits -> visitRepo.doneVisits(shift = filter.shift, classCategory = filter.classCategory)
                VisitPlanningTab.Pending -> visitRepo.pendingRequests(filter.shift, filter.classCategory)
            }
        }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            query.flatMapLatest { (tab, filter) ->
                when (tab) {
                    VisitPlanningTab.Visits -> visitRepo.doneCount(shift = filter.shift, classCategory = filter.classCategory)
                    VisitPlanningTab.Pending -> visitRepo.pendingRequestCount(filter.shift, filter.classCategory)
                }
            }.collect { count -> _state.update { it.copy(resultCount = count) } }
        }
    }

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
            VisitPlanningEvents.RequestVisit -> requestVisits()
        }
    }

    /** One pending request per selected visit, for the chosen date; it is uploaded later with the rest. */
    private fun requestVisits() {
        val selected = _state.value
        if (!selected.hasSelection) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            selected.selectedIds.forEach { id ->
                visitRepo.get(id)?.let { source ->
                    visitRepo.save(source.asPlanRequest(UUID.randomUUID().toString(), selected.scheduleDateMillis, now))
                }
            }
            _state.update { it.copy(selectedIds = emptySet(), overlay = VisitPlanningOverlay.None) }
            sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.visit_planning_requested)))
        }
    }

    private fun Set<String>.toggle(id: String): Set<String> = if (id in this) this - id else this + id
}
