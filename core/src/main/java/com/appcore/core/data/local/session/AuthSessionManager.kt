package com.appcore.core.data.local.session

import android.util.Log
import com.appcore.core.domain.models.UserAuthStatus
import com.appcore.core.data.local.datastore.model.UserDS
import com.appcore.core.domain.repo.AuthSessionLocalRepo
import com.appcore.core.domain.repo.UserLocalRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthSessionManager"

/** The session: an in-memory copy of the stored tokens for OkHttp interceptors, and the signed-in user. */
@Singleton
class AuthSessionManager @Inject constructor(
    private val authSessionLocalRepo: AuthSessionLocalRepo,
    private val userLocalRepo: UserLocalRepo<UserDS>,
    private val clearListeners: Set<@JvmSuppressWildcards SessionClearListener>,
) {
    @Volatile
    private var cachedAccess = ""

    @Volatile
    private var cachedRefresh = ""

    private val _sessionExpired = MutableSharedFlow<String?>(extraBufferCapacity = 1)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Emits (with an optional server message) when the
     * session ends without the user logging out. */
    val sessionExpired: SharedFlow<String?> = _sessionExpired.asSharedFlow()
    val user: StateFlow<UserDS> = userLocalRepo.getUser()
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = UserDS()
        )
    suspend fun initialize() {
        cachedAccess = authSessionLocalRepo.getAccessToken()
        cachedRefresh = authSessionLocalRepo.getRefreshToken()
        userLocalRepo.getUser().first()
    }

    /** Login: saves the tokens and the user together. */
    suspend fun signIn(access: String, refresh: String, user: UserDS) {
        saveSession(access, refresh)
        userLocalRepo.saveUser(user)
    }

    suspend fun updateUser(transform: (UserDS) -> UserDS) {
        userLocalRepo.updateUser(transform)
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
        userLocalRepo.clear()
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
