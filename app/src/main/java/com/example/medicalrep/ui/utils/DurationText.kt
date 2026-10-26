package com.example.medicalrep.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.medicalrep.R
import kotlin.math.roundToInt

const val SECONDS_PER_MINUTE = 60
const val MINUTES_PER_HOUR = 60

/** "25 min" or "3 h 31 min" (at least one minute). */
@Composable
fun durationText(seconds: Int): String {
    val minutes = (seconds / SECONDS_PER_MINUTE.toDouble()).roundToInt().coerceAtLeast(1)
    return if (minutes < MINUTES_PER_HOUR) {
        stringResource(R.string.duration_minutes, minutes)
    } else {
        stringResource(R.string.duration_hours_minutes, minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)
    }
}
