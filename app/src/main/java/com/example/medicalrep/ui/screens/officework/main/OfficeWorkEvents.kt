package com.example.medicalrep.ui.screens.officework.main

import com.example.medicalrep.domain.models.Shift

sealed interface OfficeWorkEvents {
    data object Back : OfficeWorkEvents
    data class OnDateChange(val millis: Long) : OfficeWorkEvents
    data object ToggleWorkTypeMenu : OfficeWorkEvents
    data class OnWorkTypeSelect(val type: OfficeWorkType) : OfficeWorkEvents
    data class OnShiftSelect(val shift: Shift) : OfficeWorkEvents
    data object Submit : OfficeWorkEvents
}
