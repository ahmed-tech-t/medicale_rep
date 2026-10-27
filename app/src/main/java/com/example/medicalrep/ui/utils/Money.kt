package com.example.medicalrep.ui.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** 1388.8 -> "1,388.8", 100.0 -> "100": thousands separated, at most two decimals, Latin digits. */
fun formatMoney(amount: Double): String =
    DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US)).format(amount)
