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
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.repo.DoctorRepo
import com.example.medicalrep.domain.repo.GeoRepo
import com.example.medicalrep.ui.components.widgets.filter.FilterEvents
import com.example.medicalrep.ui.components.widgets.filter.FilterState
import com.example.medicalrep.ui.components.widgets.filter.clearAll
import com.example.medicalrep.ui.components.widgets.filter.reduce
import com.example.medicalrep.ui.screens.visitlog.utils.toVisitListFilter
import androidx.compose.foundation.text.input.clearText
import kotlinx.coroutines.flow.flowOf
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
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

/** The rep may type fast: the list waits this long after the last key. */
private const val SEARCH_DEBOUNCE_MILLIS = 300L

/** Choosing another city drops the district, which belonged to the old one. */
private fun FilterState.change(event: FilterEvents): FilterState {
    val next = reduce(event)
    if (event !is FilterEvents.OnCitySelect) return next
    next.regionState.clearText()
    return next.copy(selectedRegion = null)
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class VisitLogViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val draftVisitRepo: DraftVisitRepo,
    private val visitRepo: VisitRepo,
    private val geoRepo: GeoRepo,
    private val doctorRepo: DoctorRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state = MutableStateFlow(VisitLogState())
    val state: StateFlow<VisitLogState> = _state.asStateFlow()

    /** One paged list per tab: a tab never shows the items of the tab before it, and a tab seen before shows its page at once. */
    private val visitsByTab = VisitLogTab.entries.associateWith(::pagedVisits)

    /** The visits of [tab] with the current filter, loaded page by page from Room. */
    fun visitsOf(tab: VisitLogTab): Flow<PagingData<VisitUi>> = visitsByTab.getValue(tab)

    /** Only the selected tab uses the filter; the others wait on All, which is where switching tab puts the filter. */
    private fun pagedVisits(tab: VisitLogTab): Flow<PagingData<VisitUi>> = listFilterOf(tab)
        .flatMapLatest { filter ->
            when (tab) {
                VisitLogTab.Required -> visitRepo.requiredVisits(filter)
                VisitLogTab.Done -> visitRepo.doneVisits(filter)
            }
        }
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    /** The filter and the search text of [tab]; the text waits for a pause in typing, and clearing it does not wait. */
    private fun listFilterOf(tab: VisitLogTab): Flow<VisitListFilter> {
        val filter = _state.map { if (it.selectedTab == tab) it.filter else VisitListFilter() }.distinctUntilChanged()
        val query = _state.map { if (it.selectedTab == tab) it.query.trim() else "" }
            .distinctUntilChanged()
            .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
        return combine(filter, query) { filter, query -> filter.copy(query = query) }.distinctUntilChanged()
    }

    init {
        viewModelScope.launch {
            draftVisitRepo.draft().collect { draft -> _state.update { it.copy(hasPendingVisit = draft != null) } }
        }
        viewModelScope.launch {
            _state.map { it.selectedTab }.distinctUntilChanged().flatMapLatest { tab ->
                listFilterOf(tab).flatMapLatest { filter ->
                    when (tab) {
                        VisitLogTab.Required -> visitRepo.requiredCount(filter)
                        VisitLogTab.Done -> visitRepo.doneCount(filter)
                    }
                }
            }.collect { count -> _state.update { it.copy(resultCount = count) } }
        }
        viewModelScope.launch {
            geoRepo.cities().collect { cities -> _state.update { it.copy(sheet = it.sheet.copy(cites = cities.map(BasicModel::toUi))) } }
        }
        viewModelScope.launch {
            doctorRepo.specialties().collect { list -> _state.update { it.copy(sheet = it.sheet.copy(specialties = list.map(BasicModel::toUi))) } }
        }
        viewModelScope.launch {
            _state.map { it.sheet.selectedCity?.id }
                .distinctUntilChanged()
                .flatMapLatest { cityId -> if (cityId == null) flowOf(emptyList()) else geoRepo.districts(cityId) }
                .collect { districts -> _state.update { it.copy(sheet = it.sheet.copy(regions = districts.map(BasicModel::toUi))) } }
        }
    }

    fun onEvent(event: VisitLogEvents) {
        when (event) {
            VisitLogEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is VisitLogEvents.SelectTab -> _state.update {
                it.copy(selectedTab = event.tab, filter = VisitListFilter(), query = "", sheet = it.sheet.clearAll(), overlay = VisitLogOverlay.None)
            }
            is VisitLogEvents.StartVisit ->
                if (_state.value.hasPendingVisit) _state.update { it.copy(overlay = VisitLogOverlay.PendingVisit(event.visitId)) }
                else sendEvent(UiEvent.Navigate(MainGraph.PlanVisit(visitId = event.visitId)))

            VisitLogEvents.ConfirmPending -> confirmPending()
            is VisitLogEvents.OpenVisit -> sendEvent(UiEvent.Navigate(MainGraph.VisitDetails(visitId = event.visitId)))
            is VisitLogEvents.CancelVisit -> {
                _state.value.cancelReason.clearText()
                _state.update { it.copy(overlay = VisitLogOverlay.CancelVisit(event.visitId)) }
            }

            VisitLogEvents.ConfirmCancel -> confirmCancel()
            VisitLogEvents.OpenFilter -> _state.update { it.copy(overlay = VisitLogOverlay.Filter) }
            VisitLogEvents.DismissOverlay -> _state.update { it.copy(overlay = VisitLogOverlay.None) }
            is VisitLogEvents.OnFilterEvent -> _state.update { it.copy(sheet = it.sheet.change(event.event)) }
            is VisitLogEvents.OnSearchChange -> _state.update { it.copy(query = event.text) }
            VisitLogEvents.ApplyFilter -> _state.update {
                it.copy(filter = it.sheet.toVisitListFilter(it.selectedTab), overlay = VisitLogOverlay.None)
            }
        }
    }

    /** Needs the sheet open and a reason; the sheet closes first, so a second tap finds nothing to confirm. */
    private fun confirmCancel() {
        val cancelling = _state.value.overlay as? VisitLogOverlay.CancelVisit ?: return
        val reason = _state.value.cancelReason.text.toString().trim()
        if (reason.isEmpty()) return
        _state.update { it.copy(overlay = VisitLogOverlay.None) }
        viewModelScope.launch { visitRepo.cancelVisit(cancelling.visitId, reason) }
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
