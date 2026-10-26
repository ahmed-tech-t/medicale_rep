package com.example.medicalrep.ui.screens.home.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.ui.models.StaticsGiftsItemUi
import com.example.medicalrep.ui.models.StaticsMetricUi
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.models.VisitUi

/** A question over Home: starting a visit while one is pending, or cancelling the pending one. */
sealed interface HomeOverlay {
    data object None : HomeOverlay

    /** [next] is what the rep asked for and will happen once the pending visit is cancelled. */
    data class PendingVisit(val next: HomeEvents) : HomeOverlay
    data object DiscardPending : HomeOverlay
}

@Immutable
data class HomeState(
    val user: UserUi = UserUi(),
    val connection: ConnectionState = ConnectionState.Offline,
    val activeVisit: VisitUi? = null,
    val todayVisits: List<VisitUi> = listOf(),
    val toSyncVisits: List<VisitUi> = listOf(),
    val visitMetrics: List<StaticsMetricUi> = listOf(),
    val custodySummary: List<StaticsGiftsItemUi> = listOf(),
    val language: String = "ar",
    val isDrawerOpen: Boolean = false,
    val overlay: HomeOverlay = HomeOverlay.None,
) {

    val shouldSync: Boolean = connection == ConnectionState.Online && toSyncVisits.isNotEmpty()
}
