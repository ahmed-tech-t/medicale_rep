package com.example.medicalrep.ui.screens.home.components.nav_drawer

import androidx.annotation.StringRes
import com.example.medicalrep.R

sealed class NavDrawerItem(@StringRes val labelRes: Int) {
    data object AddVisit : NavDrawerItem(R.string.drawer_add_visit)
    data object MyVisits : NavDrawerItem(R.string.drawer_my_visits)
    data object PlanVisit : NavDrawerItem(R.string.nav_plan)
    data object Custody : NavDrawerItem(R.string.nav_custody)
    data object Products : NavDrawerItem(R.string.products)
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
            AddAccount,
            TrackReps,
            InfoCenter,
            Terms,
        )
    }
}
