package com.example.medicalrep.ui.common.location

import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.ui.utils.BaseViewModel
import com.appcore.core.data.local.session.AuthSessionManager
import androidx.lifecycle.viewModelScope
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.domain.models.withLocation
import com.example.medicalrep.domain.repo.TrackPointRepo
import kotlin.coroutines.cancellation.CancellationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LocationStatusEvents {
    data object Refresh : LocationStatusEvents
}

@HiltViewModel
class LocationStatusViewModel @Inject constructor(
    appSettingsRepo: AppSettingsRepo,
    private val locationHelper: LocationHelper,
    private val trackPointRepo: TrackPointRepo,
    private val session: AuthSessionManager,
) : BaseViewModel(appSettingsRepo) {

    private val _status = MutableStateFlow(locationHelper.status())
    val status: StateFlow<LocationStatus> = _status.asStateFlow()

    init {
        viewModelScope.launch {
            locationHelper.statusChanges().collect { _status.value = locationHelper.status() }
        }
    }

    /**
     * Saves every GPS update as the user's last location and as a track point until cancelled (the caller runs it only
     * while location is Ready). A point that cannot be stored never stops the tracking.
     */
    suspend fun trackLocation() {
        locationHelper.updates().collect { location ->
            val now = System.currentTimeMillis()
            session.updateUser { it.withLocation(location, now) }
            saveTrackPoint(location, now)
        }
    }

    private suspend fun saveTrackPoint(location: GeoPoint, timeMillis: Long) {
        val repId = session.user.value.id?.toString() ?: return // nobody is signed in: there is no rep to store the point for
        try {
            trackPointRepo.add(TrackPoint(repId = repId, latitude = location.latitude, longitude = location.longitude, timeMillis = timeMillis))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // The rep has no row in the users table yet, so the insert is refused; the next update tries again.
        }
    }

    fun onEvent(event: LocationStatusEvents) {
        when (event) {
            LocationStatusEvents.Refresh -> _status.value = locationHelper.status()
        }
    }
}
