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
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.VisitListFilter
import com.example.medicalrep.domain.repo.DoctorRepo
import com.example.medicalrep.domain.repo.GeoRepo
import com.example.medicalrep.domain.repo.SystemSettingRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.components.widgets.filter.reduce
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.models.VisitUi
import com.example.medicalrep.ui.screens.visitlog.main.SEARCH_DEBOUNCE_MILLIS
import com.example.medicalrep.ui.screens.visitlog.main.VisitLogTab
import com.example.medicalrep.ui.screens.visitlog.utils.toVisitListFilter
import com.example.medicalrep.ui.screens.visitplanning.utils.PLAN_DAYS_AHEAD
import com.example.medicalrep.ui.screens.visitplanning.utils.SETTING_PLAN_RANGE_END
import com.example.medicalrep.ui.screens.visitplanning.utils.SETTING_PLAN_RANGE_START
import com.example.medicalrep.ui.screens.visitplanning.utils.asPlanRequest
import com.example.medicalrep.ui.screens.visitplanning.utils.planDays
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class VisitPlanningViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val visitRepo: VisitRepo,
    private val settingRepo: SystemSettingRepo,
    private val geoRepo: GeoRepo,
    private val doctorRepo: DoctorRepo,
) : BaseViewModel(appSettingsRepo) {

    private val _state =
        MutableStateFlow(VisitPlanningState(days = planDays(startOfDay(System.currentTimeMillis()))))
    val state: StateFlow<VisitPlanningState> = _state.asStateFlow()

    /** The Visits filter with the search text; the text waits for a pause in typing, and clearing it does not wait. */
    private val listFilter: Flow<VisitListFilter> = combine(
        _state.map { it.filter }.distinctUntilChanged(),
        _state.map { it.query.trim() }.distinctUntilChanged()
            .debounce { if (it.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS },
    ) { filter, query -> filter.copy(query = query) }.distinctUntilChanged()

    private val approval = _state.map { it.approval }.distinctUntilChanged()

    /** One paged list per tab: a tab never shows the items of the tab before it, and a tab seen before shows its page at once. */
    private val visitsByTab = VisitPlanningTab.entries.associateWith(::pagedVisits)

    /**
     * The visits of [tab], loaded page by page from Room. Visits tab: finished visits to plan again. Pending tab: the plan
     * requests, by the admin's answer.
     */
    fun visitsOf(tab: VisitPlanningTab): Flow<PagingData<VisitUi>> = visitsByTab.getValue(tab)

    private fun pagedVisits(tab: VisitPlanningTab): Flow<PagingData<VisitUi>> = when (tab) {
        VisitPlanningTab.Visits -> listFilter.flatMapLatest { visitRepo.doneVisits(it) }
        VisitPlanningTab.Pending -> approval.flatMapLatest { visitRepo.requests(it) }
    }.map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            combine(
                _state.map { it.selectedTab }.distinctUntilChanged(),
                listFilter,
                approval
            ) { tab, filter, approval ->
                when (tab) {
                    VisitPlanningTab.Visits -> visitRepo.doneCount(filter)
                    VisitPlanningTab.Pending -> visitRepo.requestCount(approval)
                }
            }.flatMapLatest { it }
                .collect { count -> _state.update { it.copy(resultCount = count) } }
        }
        viewModelScope.launch {
            val start = settingRepo.get(SETTING_PLAN_RANGE_START).first()?.toLongOrNull()
                ?: startOfDay(System.currentTimeMillis())
            val end = settingRepo.get(SETTING_PLAN_RANGE_END).first()?.toLongOrNull()
                ?: (start + PLAN_DAYS_AHEAD * 86_400_000L)
            val count = ((end - start) / 86_400_000L).toInt().coerceAtLeast(1)
            _state.update { it.copy(days = planDays(start, count)) }
        }
        visitRepo.bookedDays()
            .onEach { _state.update { s -> s.copy(bookedDays = it) } }
            .launchIn(viewModelScope)
        geoRepo.cities()
            .onEach { cities ->
                _state.update {
                    it.copy(
                        sheet = it.sheet.copy(
                            cites = cities.map(
                                BasicModel::toUi
                            )
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
        doctorRepo.specialties()
            .onEach { list ->
                _state.update {
                    it.copy(
                        sheet = it.sheet.copy(
                            specialties = list.map(
                                BasicModel::toUi
                            )
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
        _state.map { it.sheet.selectedCity?.id }
            .distinctUntilChanged()
            .flatMapLatest { cityId ->
                if (cityId == null) flowOf(emptyList()) else geoRepo.districts(
                    cityId
                )
            }
            .onEach { districts ->
                _state.update {
                    it.copy(
                        sheet = it.sheet.copy(
                            regions = districts.map(
                                BasicModel::toUi
                            )
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: VisitPlanningEvents) {
        when (event) {
            VisitPlanningEvents.Back -> sendEvent(UiEvent.NavigationUp)
            is VisitPlanningEvents.SelectTab -> _state.update { it.copy(selectedTab = event.tab) }
            is VisitPlanningEvents.OnSearchChange -> _state.update { it.copy(query = event.text) }
            VisitPlanningEvents.OpenFilter -> _state.update { it.copy(overlay = VisitPlanningOverlay.Filter) }
            is VisitPlanningEvents.OnFilterEvent -> _state.update {
                it.copy(
                    sheet = it.sheet.reduce(
                        event.event
                    )
                )
            }

            VisitPlanningEvents.ApplyFilter -> _state.update {
                it.copy(
                    filter = it.sheet.toVisitListFilter(VisitLogTab.Required),
                    overlay = VisitPlanningOverlay.None
                )
            }

            is VisitPlanningEvents.SelectApproval -> _state.update { it.copy(approval = event.approval) }
            is VisitPlanningEvents.ToggleDay -> _state.update {
                it.copy(
                    selectedDays = it.selectedDays.toggled(
                        event.visitId,
                        event.dayMillis
                    )
                )
            }

            VisitPlanningEvents.ScheduleSelectedDays -> scheduleSelectedDays()
            is VisitPlanningEvents.PickAnotherTime -> _state.update {
                it.copy(
                    rescheduleId = event.visit.id,
                    rescheduleVisit = event.visit,
                    overlay = VisitPlanningOverlay.ScheduleVisit
                )
            }

            VisitPlanningEvents.DismissOverlay -> _state.update {
                it.copy(
                    overlay = VisitPlanningOverlay.None,
                    rescheduleId = null,
                    rescheduleVisit = null
                )
            }

            VisitPlanningEvents.RequestVisit -> requestAgain()
        }
    }

    /** One pending request per visit and picked day; they are uploaded later with the rest. */
    private fun scheduleSelectedDays() {
        val picked = _state.value.selectedDays
        if (picked.isEmpty()) return
        viewModelScope.launch {
            saveDayRequests(picked)
            _state.update { it.copy(selectedDays = emptyMap()) }
            sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.visit_planning_requested)))
        }
    }

    /** The rejected request is sent again for each day the rep picked in the sheet. */
    private fun requestAgain() {
        val s = _state.value
        val id = s.rescheduleId ?: return
        val days = s.selectedDays[id] ?: return
        if (days.isEmpty()) return
        viewModelScope.launch {
            saveDayRequests(mapOf(id to days))
            _state.update {
                it.copy(
                    rescheduleId = null,
                    rescheduleVisit = null,
                    overlay = VisitPlanningOverlay.None,
                    selectedDays = it.selectedDays - id
                )
            }
            sendEvent(UiEvent.ShowSnackBar(UiText.StringResource(R.string.visit_planning_requested)))
        }
    }

    private suspend fun saveDayRequests(picks: Map<String, Set<Long>>) {
        val now = System.currentTimeMillis()
        picks.forEach { (id, days) ->
            visitRepo.get(id)?.let { source ->
                days.forEach { day ->
                    visitRepo.save(
                        source.asPlanRequest(
                            UUID.randomUUID().toString(), day, now
                        )
                    )
                }
            }
        }
    }
}

/** [dayMillis] is picked for [visitId], or not any more when it was; a visit with no day left leaves the map. */
private fun Map<String, Set<Long>>.toggled(
    visitId: String,
    dayMillis: Long
): Map<String, Set<Long>> {
    val days = getOrDefault(visitId, emptySet()).toggle(dayMillis)
    return if (days.isEmpty()) this - visitId else this + (visitId to days)
}
