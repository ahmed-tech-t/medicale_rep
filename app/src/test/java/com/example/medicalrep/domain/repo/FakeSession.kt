package com.example.medicalrep.domain.repo

import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.LocationFix
import com.example.medicalrep.domain.models.withLocation
import kotlinx.coroutines.runBlocking

/** A session for tests, already initialized with [user]; sign in or out through [manager]. */
class FakeSession(user: UserDS = UserDS()) {
    val users = FakeUserLocalRepo(user)
    val manager = AuthSessionManager(NoTokensRepo, users, emptySet()).also { runBlocking { it.initialize() } }

    /** Puts [point] (or no location) into the signed-in user, as the GPS tracking does. */
    fun setLocation(point: GeoPoint?, accuracyMeters: Float = 5f) = runBlocking {
        manager.updateUser {
            if (point == null) it.copy(lastLatitude = null, lastLongitude = null, lastAccuracyMeters = null)
            else it.withLocation(LocationFix(point, accuracyMeters), 1L)
        }
    }

    /** Puts [address] (or no address) into the signed-in user, as the GPS tracking does. */
    fun setAddress(address: String?) = runBlocking { manager.updateUser { it.copy(lastAddress = address) } }

    private object NoTokensRepo : AuthSessionLocalRepo {
        override suspend fun getAccessToken() = ""
        override suspend fun getRefreshToken() = ""
        override suspend fun saveSession(accessToken: String, refreshToken: String) = Unit
        override suspend fun clearSession() = Unit
    }
}
