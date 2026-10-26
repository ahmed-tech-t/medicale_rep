package com.example.medicalrep.ui.common.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

/** True when either precise or approximate location is granted. */
fun Context.hasLocationPermission(): Boolean = LOCATION_PERMISSIONS.any {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/** [onAnswered] runs when the user has answered the system prompt (also at once when it cannot be shown again). */
@Composable
fun rememberLocationPermissionLauncher(onAnswered: () -> Unit = {}): ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { onAnswered() }

/** Opens the screen where the user can fix [status]: app permissions or the GPS switch. */
fun Context.openLocationSettings(status: LocationStatus) {
    val intent = when (status) {
        LocationStatus.PermissionDenied ->
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        LocationStatus.GpsOff -> Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        LocationStatus.Ready -> return
    }
    startActivity(intent)
}
