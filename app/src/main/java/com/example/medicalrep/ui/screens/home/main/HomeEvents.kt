package com.example.medicalrep.ui.screens.home.main

import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem

sealed interface HomeEvents {
    data object OnMenuClick : HomeEvents
    data object OnDrawerDismiss : HomeEvents
    data class OnDrawerItem(val item: NavDrawerItem) : HomeEvents
    data object OnSyncClick : HomeEvents
    data object OnActiveContinue : HomeEvents
    data object OnActiveCancel : HomeEvents
    data class OnStartVisit(val visitId: String) : HomeEvents
    data object OnTodayMoreClick : HomeEvents
    data object OnAddVisitClick : HomeEvents
    data object OnPendingVisitConfirm : HomeEvents
    data object OnDiscardPendingConfirm : HomeEvents
    data object OnOverlayDismiss : HomeEvents
}
