package com.example.medicalrep.ui.screens.officework.main

import androidx.compose.foundation.text.input.TextFieldState
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.ui.utils.startOfDay

data class OfficeWorkState(
    val dateMillis: Long = startOfDay(System.currentTimeMillis()),
    val workType: OfficeWorkType = OfficeWorkType.MeetingSupervisor,
    val shift: Shift = Shift.Full,
    val notes: TextFieldState = TextFieldState(),
    val showWorkTypeMenu: Boolean = false,
)
