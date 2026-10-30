package com.example.medicalrep.ui.screens.visitplanning.utils

import androidx.compose.runtime.Immutable
import com.example.medicalrep.ui.utils.startOfNextDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** How many days ahead a visit card offers when the server has not sent a range. */
const val PLAN_DAYS_AHEAD = 14

/** `system_settings` keys for the plan date range sent by the server (epoch millis as String). */
const val SETTING_PLAN_RANGE_START = "plan_range_start"
const val SETTING_PLAN_RANGE_END = "plan_range_end"

/** One day a visit can be planned for: [millis] is the start of the day. */
@Immutable
data class PlanDay(val millis: Long, val weekday: String, val date: String)

/** [count] days from the day starting at [firstDayMillis], each named for its tile ("Monday", "02/11"). */
fun planDays(firstDayMillis: Long, count: Int = PLAN_DAYS_AHEAD): List<PlanDay> {
    val weekday = SimpleDateFormat("EEEE", Locale.getDefault())
    val date = SimpleDateFormat("dd/MM", Locale.getDefault())
    return generateSequence(firstDayMillis, ::startOfNextDay).take(count).map {
        PlanDay(it, weekday.format(Date(it)), date.format(Date(it)))
    }.toList()
}
