package com.example.medicalrep.ui.screens.visitlog.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.repo.DraftVisitRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitFilter
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.navigation.model.MainGraph
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
import javax.inject.Inject

/** The tab and filter that decide which visits the repo is asked for. */
private data class VisitLogQuery(val tab: VisitLogTab, val filter: VisitFilter)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VisitLogViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val draftVisitRepo: DraftVisitRepo,
    private val visitRepo: VisitRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(VisitLogState())
    val state: StateFlow<VisitLogState> = _state.asStateFlow()

    private val query = _state.map { VisitLogQuery(it.selectedTab, it.filter) }.distinctUntilChanged()

    /** One paged list per tab: a tab never shows the items of the tab before it, and a tab seen before shows its page at once. */
    private val visitsByTab = VisitLogTab.entries.associateWith(::pagedVisits)

    /** The visits of [tab] with the current filter, loaded page by page from Room. */
    fun visitsOf(tab: VisitLogTab): Flow<PagingData<VisitUi>> = visitsByTab.getValue(tab)

    /** Only the selected tab uses the filter; the others wait on All, which is where switching tab puts the filter. */
    private fun pagedVisits(tab: VisitLogTab): Flow<PagingData<VisitUi>> = _state
        .map { if (it.selectedTab == tab) it.filter else VisitFilter.All }
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            when (tab) {
                VisitLogTab.Required -> visitRepo.requiredVisits(filter.shift, filter.classCategory)
                VisitLogTab.Done -> visitRepo.doneVisits(filter.shift, filter.doubleOnly, filter.syncStatus)
            }
        }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            draftVisitRepo.draft().collect { draft -> _state.update { it.copy(hasPendingVisit = draft != null) } }
        }
        viewModelScope.launch {
            query.flatMapLatest { (tab, filter) ->
                when (tab) {
                    VisitLogTab.Required -> visitRepo.requiredCount(filter.shift, filter.classCategory)
                    VisitLogTab.Done -> visitRepo.doneCount(filter.shift, filter.doubleOnly, filter.syncStatus)
                }
            }.collect { count -> _state.update { it.copy(resultCount = count) } }
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
