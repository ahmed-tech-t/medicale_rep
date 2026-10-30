package com.example.medicalrep.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Time of day like "6:09 PM" in the app language. */
fun formatClockTime(millis: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))

/** Date like "17/10/2026". */
fun formatDate(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))

/** Weekday and day like "Monday 02/11". */
fun formatDayChip(millis: Long): String = SimpleDateFormat("EEEE dd/MM", Locale.getDefault()).format(Date(millis))

/** The first millisecond of the local day [millis] falls on. */
fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

/** The first millisecond of the day after the one [millis] falls on (a day is 23 to 25 hours around a clock change). */
fun startOfNextDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = startOfDay(millis)
    add(Calendar.DAY_OF_YEAR, 1)
}.timeInMillis

/** The weekday and date like "الاثنين، 26 أكتوبر" or "Monday, October 26", in the app language. */
fun formatWeekdayDate(millis: Long): String {
    val pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEEdMMMM")
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
}

/** The Saturday at midnight that starts the week [millis] falls in (the week runs Saturday to Friday). */
fun startOfWeek(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = startOfDay(millis)
    // Calendar numbers Sunday 1 .. Saturday 7, so Saturday is 0 days back, Sunday 1, ... Friday 6.
    add(Calendar.DAY_OF_YEAR, -((get(Calendar.DAY_OF_WEEK) - Calendar.SATURDAY + 7) % 7))
}.timeInMillis

/** The Saturday at midnight that starts the week after the one [millis] falls in. */
fun startOfNextWeek(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = startOfWeek(millis)
    add(Calendar.DAY_OF_YEAR, 7)
}.timeInMillis

/** How long a visit took, as shown on its card: 275_000 -> "04:35 د". */
fun formatDurationLabel(durationMillis: Long): String {
    val seconds = durationMillis.coerceAtLeast(0) / 1000
    return String.format(Locale.US, "%02d:%02d د", seconds / 60, seconds % 60)
}
