package com.example.medicalrep.data.repo

import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.domain.repo.UserLocalRepo
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.LastLocationRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Keeps the location inside the user's DataStore, so it is cleared with the user. */
class LastLocationRepoImpl @Inject constructor(
    private val userRepo: UserLocalRepo<UserDS>,
) : LastLocationRepo {

    override val lastLocation: Flow<GeoPoint?> = userRepo.getUser()
        .map { user ->
            val latitude = user.lastLatitude
            val longitude = user.lastLongitude
            if (latitude != null && longitude != null) GeoPoint(latitude, longitude) else null
        }
        .distinctUntilChanged()

    override suspend fun current(): GeoPoint? = lastLocation.first()

    override suspend fun save(location: GeoPoint, timeMillis: Long) {
        userRepo.updateUser {
            it.copy(lastLatitude = location.latitude, lastLongitude = location.longitude, lastLocationTimeMillis = timeMillis)
        }
    }
}
