package com.example.medicalrep.ui.screens.home.main

import com.example.medicalrep.ui.screens.home.components.nav_drawer.NavDrawerItem

sealed interface HomeEvents {
    data class OnDrawerItem(val item: NavDrawerItem) : HomeEvents
    data object OnActiveContinue : HomeEvents
    data object OnActiveCancel : HomeEvents
    data class OnStartVisit(val visitId: String) : HomeEvents
    data object OnTodayMoreClick : HomeEvents
    data object OnAddVisitClick : HomeEvents
    data object OnPlanVisitsClick : HomeEvents
    data object OnAccountsClick : HomeEvents
    data object OnOfficeWorkClick : HomeEvents
    data object OnPendingVisitConfirm : HomeEvents
    data object OnDiscardPendingConfirm : HomeEvents
    data object OnNotificationsClick : HomeEvents
    data object OnOverlayDismiss : HomeEvents
}
