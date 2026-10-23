package com.appcore.core.data.remote.interceptor

import android.util.Log
import com.appcore.core.data.local.session.AuthSessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "TokenAuthenticator"

/** On a 401, refreshes the tokens once (shared by parallel requests) and retries the request. */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val authSessionManager: AuthSessionManager,
    private val tokenRefresher: TokenRefresher,
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null
        if (responseCount(response) >= 2) return null
        // The token the failed request was actually sent with (the cache may already hold a newer one)
        val failedAccess = response.request.header("Authorization")?.removePrefix("Bearer ").orEmpty()
        // Not signed in → nothing to refresh
        if (failedAccess.isBlank()) return null
        return runBlocking {
            mutex.withLock {
                // Parallel 401 may have already refreshed
                val latestAccess = authSessionManager.getAccessTokenSync()
                if (latestAccess.isNotBlank() && latestAccess != failedAccess) {
                    return@withLock retryWithBearer(response, latestAccess)
                }
                val refreshToken = authSessionManager.getRefreshTokenSync()
                if (refreshToken.isBlank()) {
                    authSessionManager.onSessionExpired()
                    return@withLock null
                }
                when (val result = refreshTokens(refreshToken)) {
                    is RefreshResult.Success -> {
                        authSessionManager.saveSession(result.accessToken, result.refreshToken)
                        retryWithBearer(response, result.accessToken)
                    }

                    RefreshResult.InvalidSession -> {
                        authSessionManager.onSessionExpired()
                        null
                    }

                    RefreshResult.TransientFailure -> null
                }
            }
        }
    }

    private suspend fun refreshTokens(refreshToken: String): RefreshResult {
        return try {
            val result = tokenRefresher.refresh(refreshToken)
            if (result is RefreshResult.Success &&
                (result.accessToken.isBlank() || result.refreshToken.isBlank())
            ) {
                RefreshResult.InvalidSession
            } else {
                result
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            when (e.code()) {
                401, 403 -> RefreshResult.InvalidSession
                else -> RefreshResult.TransientFailure
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshTokens: ${e.message}")
            RefreshResult.TransientFailure
        }
    }

    private fun retryWithBearer(response: Response, accessToken: String): Request {
        return response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
