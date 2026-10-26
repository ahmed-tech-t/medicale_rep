package com.example.medicalrep.ui.common.location

enum class LocationStatus { Ready, PermissionDenied, GpsOff }

/**
 * Our blocking dialog comes after the system permission prompt: a missing permission only counts once that prompt
 * was answered ([permissionPrompted]); GPS being off needs no system prompt, so it shows at once.
 */
fun locationDialogNeeded(isMain: Boolean, status: LocationStatus, permissionPrompted: Boolean): Boolean = isMain && when (status) {
    LocationStatus.Ready -> false
    LocationStatus.PermissionDenied -> permissionPrompted
    LocationStatus.GpsOff -> true
}
