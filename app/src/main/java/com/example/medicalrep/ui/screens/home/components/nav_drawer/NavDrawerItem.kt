package com.example.medicalrep.ui.screens.home.components.nav_drawer

import androidx.annotation.StringRes
import com.appcore.core.domain.utils.UiEvent
import com.example.medicalrep.R
import com.example.medicalrep.ui.navigation.model.MainGraph
import com.example.medicalrep.ui.screens.home.main.HomeEvents


sealed class NavDrawerItem(@StringRes val labelRes: Int) {
    data object AddVisit : NavDrawerItem(R.string.drawer_add_visit)
    data object MyVisits : NavDrawerItem(R.string.drawer_my_visits)
    data object PlanVisit : NavDrawerItem(R.string.nav_plan)
    data object Custody : NavDrawerItem(R.string.nav_custody)
    data object Products : NavDrawerItem(R.string.products)
    data object Orders : NavDrawerItem(R.string.orders)
    data object Statistics : NavDrawerItem(R.string.nav_statics)
    data object Accounts : NavDrawerItem(R.string.accounts)
    data object AddAccount : NavDrawerItem(R.string.drawer_add_account)
    data object TrackReps : NavDrawerItem(R.string.track_rep)
    data object InfoCenter : NavDrawerItem(R.string.drawer_info_center)
    data object Terms : NavDrawerItem(R.string.terms_and_conditions)
    data object Language : NavDrawerItem(R.string.language)

    companion object {
        val menu: List<NavDrawerItem> = listOf(
            AddVisit,
            MyVisits,
            PlanVisit,
            Custody,
            Products,
            Orders,
            Statistics,
            Accounts,
            AddAccount,
            TrackReps,
            InfoCenter,
            Terms,
        )
    }
}

fun NavDrawerItem.nav(sendEvent: (UiEvent) -> Unit) {
    when (this) {
        NavDrawerItem.Products -> sendEvent(UiEvent.Navigate(MainGraph.Products))
        NavDrawerItem.Orders -> sendEvent(UiEvent.Navigate(MainGraph.Orders))
        NavDrawerItem.Statistics -> sendEvent(UiEvent.Navigate(MainGraph.Statistics))
        NavDrawerItem.Accounts -> sendEvent(UiEvent.Navigate(MainGraph.Accounts))
        NavDrawerItem.PlanVisit -> sendEvent(UiEvent.Navigate(MainGraph.VisitPlanning))
        NavDrawerItem.TrackReps -> sendEvent(UiEvent.Navigate(MainGraph.RepTracking))
        NavDrawerItem.MyVisits -> sendEvent(UiEvent.Navigate(MainGraph.VisitLog))
        NavDrawerItem.Custody -> sendEvent(UiEvent.Navigate(MainGraph.SampleAndGifts))
        NavDrawerItem.AddAccount -> sendEvent(UiEvent.Navigate(MainGraph.AddAccount))
        NavDrawerItem.Terms -> sendEvent(UiEvent.Navigate(MainGraph.Terms))
        NavDrawerItem.InfoCenter -> sendEvent(UiEvent.Navigate(MainGraph.DataCenter))
        NavDrawerItem.AddVisit, NavDrawerItem.Language -> Unit // handled by the ViewModel
    }
}