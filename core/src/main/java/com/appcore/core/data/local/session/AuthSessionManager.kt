package com.appcore.core.data.local.session

import android.util.Log
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthSessionManager"

/** In-memory copy of the stored tokens so OkHttp interceptors can read them synchronously. */
@Singleton
class AuthSessionManager @Inject constructor(
    private val authSessionLocalRepo: AuthSessionLocalRepo,
    private val clearListeners: Set<@JvmSuppressWildcards SessionClearListener>,
) {
    @Volatile
    private var cachedAccess = ""

    @Volatile
    private var cachedRefresh = ""

    private val _sessionExpired = MutableSharedFlow<String?>(extraBufferCapacity = 1)

    /** Emits (with an optional server message) when the session ends without the user logging out. */
    val sessionExpired: SharedFlow<String?> = _sessionExpired.asSharedFlow()

    suspend fun initialize() {
        cachedAccess = authSessionLocalRepo.getAccessToken()
        cachedRefresh = authSessionLocalRepo.getRefreshToken()
    }

    suspend fun saveSession(access: String, refresh: String) {
        Log.d(TAG, "saveSession: token saved successfully")
        authSessionLocalRepo.saveSession(access, refresh)
        cachedAccess = access
        cachedRefresh = refresh
    }

    suspend fun onSessionExpired(message: String? = null) {
        clearSession()
        _sessionExpired.emit(message)
    }


    suspend fun clearSession() {
        Log.d(TAG, "clearSession: token cleared successfully")
        authSessionLocalRepo.clearSession()
        cachedAccess = ""
        cachedRefresh = ""
        clearListeners.forEach { it.onSessionCleared() }
    }


    fun isAuthenticated() = cachedAccess.isNotBlank()

    fun getUserAuthStatus(): UserAuthStatus {
        return if (isAuthenticated()) UserAuthStatus.Authenticated
        else UserAuthStatus.NotAuthenticated
    }

    fun getAccessTokenSync() = cachedAccess
    fun getRefreshTokenSync() = cachedRefresh
}
