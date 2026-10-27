package com.example.medicalrep.ui.common.location

/** How often the GPS may report, and how far the phone must move before it does. */
const val UPDATE_INTERVAL_MS = 5_000L
const val MIN_UPDATE_DISTANCE_METERS = 5f

/** A GPS reading that may be off by more than this many metres counts as a weak signal. */
const val WEAK_GPS_METERS = 20f

/** A visit recorded within this distance of the account counts as made at the account. */
const val MAX_MATCH_DISTANCE_METERS = 50
