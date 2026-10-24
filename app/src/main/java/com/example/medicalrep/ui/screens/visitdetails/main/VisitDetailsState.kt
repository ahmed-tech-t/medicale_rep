package com.example.medicalrep.ui.screens.visitdetails.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.models.VisitUi

@Immutable
data class VisitDetailsState(
    val visit: VisitUi = VisitUi(),
)
