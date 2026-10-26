package com.example.medicalrep.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Time of day like "6:09 PM" in the app language. */
fun formatClockTime(millis: Long): String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))

/** Date like "17/10/2026". */
fun formatDate(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
