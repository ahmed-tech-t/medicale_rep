package com.example.medicalrep.domain.models

import androidx.compose.runtime.Immutable
import kotlin.math.roundToInt

/** How many of the planned visits are done, and how many were missed. */
@Immutable
data class VisitProgress(val completed: Int = 0, val planned: Int = 0, val missed: Int = 0) {
    /** The share done as a whole percent: 0 with nothing planned, never above 100. */
    val percent: Int get() = if (planned == 0) 0 else (completed * 100f / planned).roundToInt().coerceAtMost(100)
}
