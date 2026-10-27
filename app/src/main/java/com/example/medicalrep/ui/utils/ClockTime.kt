package com.example.medicalrep.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Time of day like "6:09 PM" in the app language. */
fun formatClockTime(millis: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))

/** Date like "17/10/2026". */
fun formatDate(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))

/** The first millisecond of the local day [millis] falls on. */
fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

/** How long a visit took, as shown on its card: 275_000 -> "04:35 د". */
fun formatDurationLabel(durationMillis: Long): String {
    val seconds = durationMillis.coerceAtLeast(0) / 1000
    return String.format(Locale.US, "%02d:%02d د", seconds / 60, seconds % 60)
}
