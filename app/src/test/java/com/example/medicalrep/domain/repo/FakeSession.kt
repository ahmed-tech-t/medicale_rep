package com.example.medicalrep.domain.repo

import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.data.local.session.AuthSessionManager
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.models.withLocation
import kotlinx.coroutines.runBlocking

/** A session for tests, already initialized with [user]; sign in or out through [manager]. */
class FakeSession(user: UserDS = UserDS()) {
    val users = FakeUserLocalRepo(user)
    val manager = AuthSessionManager(NoTokensRepo, users, emptySet()).also { runBlocking { it.initialize() } }

    /** Puts [point] (or no location) into the signed-in user, as the GPS tracking does. */
    fun setLocation(point: GeoPoint?) = runBlocking {
        manager.updateUser {
            if (point == null) it.copy(lastLatitude = null, lastLongitude = null) else it.withLocation(point, 1L)
        }
    }

    private object NoTokensRepo : AuthSessionLocalRepo {
        override suspend fun getAccessToken() = ""
        override suspend fun getRefreshToken() = ""
        override suspend fun saveSession(accessToken: String, refreshToken: String) = Unit
        override suspend fun clearSession() = Unit
    }
}
