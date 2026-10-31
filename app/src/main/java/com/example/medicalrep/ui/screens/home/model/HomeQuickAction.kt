package com.example.medicalrep.ui.screens.home.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.medicalrep.R
import com.example.medicalrep.ui.screens.home.main.HomeEvents

data class HomeQuickAction(
    @StringRes val title: Int,
    val icon: ImageVector,
    val event: HomeEvents?,
    val primary: Boolean = false,
)

val HomeQuickActions = listOf(
    HomeQuickAction(
        R.string.home_add_visit,
        Icons.Outlined.Add,
        HomeEvents.OnAddVisitClick,
        primary = true
    ),
    HomeQuickAction(
        R.string.home_plan_visits,
        Icons.Outlined.CalendarMonth,
        HomeEvents.OnPlanVisitsClick
    ),
    HomeQuickAction(R.string.home_office_work, Icons.Outlined.BusinessCenter, HomeEvents.OnOfficeWorkClick),
    HomeQuickAction(R.string.home_accounts, Icons.Outlined.MedicalServices, HomeEvents.OnAccountsClick),
)