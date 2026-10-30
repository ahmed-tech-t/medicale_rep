package com.example.medicalrep.ui.screens.home.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.ConnectionState
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.ui.models.UserUi
import com.example.medicalrep.ui.models.VisitUi

/** A question over Home (starting a visit while one is pending, or cancelling the pending one). */
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
    /** The street the rep is on; null when it is not known. */
    val address: String? = null,
    /** The day Home was opened, for the date line. */
    val dateMillis: Long = 0L,
    /** The visits done out of the visits planned this week. */
    val progress: VisitProgress = VisitProgress(),
    val activeVisit: VisitUi? = null,
    val todayVisits: List<VisitUi> = listOf(),
    /** The bell's number: notifications the rep has not read. */
    val unreadCount: Int = 0,
    val language: String = "ar",
    val overlay: HomeOverlay = HomeOverlay.None,
)
