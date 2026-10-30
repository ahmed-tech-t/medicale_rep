package com.example.medicalrep.ui.screens.home.main

import androidx.lifecycle.viewModelScope
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.local.settings.LocaleHelper
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.UiEvent
import com.appcore.core.domain.utils.UiEvent.*
import com.appcore.core.ui.utils.BaseViewModel
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.repo.DraftVisitRepo
import com.example.medicalrep.domain.repo.NotificationRepo
import com.example.medicalrep.domain.repo.StatisticsRepo
import com.example.medicalrep.domain.repo.VisitRepo
import com.example.medicalrep.ui.common.connectivity.ConnectivityObserver
import com.example.medicalrep.ui.mapper.toUi
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.navigation.model.MainGraph.*
import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem
import com.example.medicalrep.ui.screens.home.components.nav_drawer.nav
import com.example.medicalrep.domain.models.lastLocation
import com.example.medicalrep.ui.utils.startOfDay
import com.example.medicalrep.ui.utils.startOfNextDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Home lists the next few visits (today's first, the nearest first); the rest are under View all. */
private const val TODAY_VISITS_LIMIT = 3

private fun GeoPoint.rounded() = GeoPoint(Math.round(latitude * 1000) / 1000.0, Math.round(longitude * 1000) / 1000.0)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val appSettingsManager: AppSettingsManager,
    private val localeHelper: LocaleHelper,
    connectivityObserver: ConnectivityObserver,
    private val draftVisitRepo: DraftVisitRepo,
    private val visitRepo: VisitRepo,
    statisticsRepo: StatisticsRepo,
    notificationRepo: NotificationRepo,
    private val session: AuthSessionManager,
) : BaseViewModel(appSettingsRepo) {

    /** When Home opened: today and this week are fixed from then. */
    private val openedAtMillis = System.currentTimeMillis()

    // Offline until the observer reports the real network state.
    private val _state = MutableStateFlow(
        HomeState(
            language = appSettingsManager.getLanguageSync(),
            dateMillis = openedAtMillis
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch { markMissedOncePerDay() }
        viewModelScope.launch {
            draftVisitRepo.draft()
                .collect { draft -> _state.update { it.copy(activeVisit = draft?.visit?.toUi()) } }
        }
        viewModelScope.launch {
            connectivityObserver.connection.collect { connection ->
                _state.update {
                    it.copy(
                        connection = connection
                    )
                }
            }
        }
        viewModelScope.launch {
            session.user.collect { user ->
                _state.update {
                    it.copy(
                        user = user.toUi(),
                        address = user.lastAddress,
                    )
                }
            }
        }
        viewModelScope.launch {
            statisticsRepo.progress(startOfDay(openedAtMillis), startOfNextDay(openedAtMillis))
                .collect { progress -> _state.update { it.copy(progress = progress) } }
        }
        viewModelScope.launch {
            // Room orders them (today first, nearest first); the point is rounded to about 100 m so only a real move asks again.
            session.user.map { it.lastLocation?.rounded() }
                .distinctUntilChanged()
                .flatMapLatest { location -> visitRepo.scheduledFrom(startOfDay(openedAtMillis), location, TODAY_VISITS_LIMIT) }
                .collect { visits -> _state.update { it.copy(todayVisits = visits.map(Visit::toUi)) } }
        }
        viewModelScope.launch {
            session.user
                .map { it.id?.toString() }
                .distinctUntilChanged()
                .flatMapLatest { repId ->
                    if (repId == null) flowOf(0) else notificationRepo.unreadCount(
                        repId
                    )
                }
                .collect { count -> _state.update { it.copy(unreadCount = count) } }
        }
    }

    /** Scheduled visits of earlier days were not done: mark them Missed the first time Home opens each day. */
    private suspend fun markMissedOncePerDay() {
        val today = startOfDay(System.currentTimeMillis())
        if (session.user.value.missedCheckedDay == today) return
        visitRepo.markMissed(today)
        session.updateUser { it.copy(missedCheckedDay = today) }
    }

    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.OnOverlayDismiss -> _state.update { it.copy(overlay = HomeOverlay.None) }
            is HomeEvents.OnDrawerItem -> {
                when (event.item) {
                    NavDrawerItem.Language -> switchLanguage()
                    NavDrawerItem.AddVisit -> requestStart(HomeEvents.OnAddVisitClick)
                    else -> event.item.nav(::sendEvent)
                }
            }

            HomeEvents.OnAddVisitClick, is HomeEvents.OnStartVisit -> requestStart(event)
            HomeEvents.OnActiveContinue -> sendEvent(
                Navigate(
                    PlanVisit(
                        resumeDraft = true
                    )
                )
            )

            HomeEvents.OnActiveCancel -> _state.update { it.copy(overlay = HomeOverlay.DiscardPending) }
            HomeEvents.OnPendingVisitConfirm -> confirmPending()
            HomeEvents.OnDiscardPendingConfirm -> viewModelScope.launch {
                draftVisitRepo.discard()
                _state.update { it.copy(overlay = HomeOverlay.None) }
            }

            HomeEvents.OnPlanVisitsClick -> sendEvent(Navigate(MainGraph.VisitPlanning))
            HomeEvents.OnAccountsClick -> sendEvent(Navigate(MainGraph.Accounts))
            HomeEvents.OnTodayMoreClick -> sendEvent(Navigate(MainGraph.VisitLog))
        }
    }

    /** Starting a visit while one is pending asks first; otherwise it starts at once. */
    private fun requestStart(event: HomeEvents) {
        if (_state.value.activeVisit != null) _state.update {
            it.copy(
                overlay = HomeOverlay.PendingVisit(
                    event
                )
            )
        }
        else startVisit(event)
    }

    private fun startVisit(event: HomeEvents) {
        when (event) {
            is HomeEvents.OnStartVisit -> sendEvent(UiEvent.Navigate(MainGraph.PlanVisit(visitId = event.visitId)))
            else -> openPlanVisit()
        }
    }

    private fun confirmPending() {
        val next = (_state.value.overlay as? HomeOverlay.PendingVisit)?.next ?: return
        viewModelScope.launch {
            draftVisitRepo.discard()
            _state.update { it.copy(overlay = HomeOverlay.None) }
            startVisit(next)
        }
    }

    private fun openPlanVisit() {
        sendEvent(UiEvent.Navigate(MainGraph.PlanVisit()))
    }

    private fun switchLanguage() {
        val next = if (_state.value.language == "ar") "en" else "ar"
        _state.update { it.copy(language = next) }
        viewModelScope.launch {
            appSettingsManager.updateLanguage(next)
            localeHelper.setLocale(next)
        }
    }

}
