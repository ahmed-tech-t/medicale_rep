package com.example.medicalrep.ui.common.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.tasks.Task
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.medicalrep.domain.models.map.MapCameraTarget
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

private const val GPS_FIX_TIMEOUT_MS = 10_000L

@Singleton
class FusedLocationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationHelper {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun status(): LocationStatus = when {
        !context.hasLocationPermission() -> LocationStatus.PermissionDenied
        context.getSystemService<LocationManager>()?.isLocationEnabled != true -> LocationStatus.GpsOff
        else -> LocationStatus.Ready
    }

    override fun statusChanges(): Flow<Unit> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(Unit)
            }
        }
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
            addAction(LocationManager.MODE_CHANGED_ACTION)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        awaitClose { context.unregisterReceiver(receiver) }
    }

    @SuppressLint("MissingPermission") // Checked by hasLocationPermission().
    override suspend fun getLocation(): MapCameraTarget? {
        if (!context.hasLocationPermission()) return null
        val current = withTimeoutOrNull(GPS_FIX_TIMEOUT_MS.milliseconds) {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).awaitOrNull()
        }
        return current?.toTarget() ?: getLastLocation()
    }

    @SuppressLint("MissingPermission") // Only called after getLocation() checked the permission.
    private suspend fun getLastLocation(): MapCameraTarget? = client.lastLocation.awaitOrNull()?.toTarget()

    private fun Location.toTarget() = MapCameraTarget(latitude, longitude)
}

/** A failed task (e.g. location services error) counts as "no location" so the caller can fall back. */
private suspend fun <T> Task<T>.awaitOrNull(): T? = try {
    await()
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    null
}
