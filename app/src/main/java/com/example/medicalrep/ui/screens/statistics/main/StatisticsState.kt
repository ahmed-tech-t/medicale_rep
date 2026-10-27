package com.example.medicalrep.ui.screens.statistics.main

import androidx.compose.runtime.Immutable
import com.example.medicalrep.domain.models.Statistics

/** What the Statistics page shows; zeros until the first numbers arrive. */
@Immutable
data class StatisticsState(
    val statistics: Statistics = Statistics(),
)
