package com.appcore.core.data.remote.interceptor

/**
 * The app's refresh-token API call, bound with Hilt `@Binds`. Use the `@RefreshClient` Retrofit
 * so the call skips the auth interceptors. Thrown `HttpException`/`IOException` are mapped by
 * [TokenAuthenticator], so implementations only map the response body.
 */
fun interface TokenRefresher {
    suspend fun refresh(refreshToken: String): RefreshResult
}

sealed interface RefreshResult {
    data class Success(val accessToken: String, val refreshToken: String) : RefreshResult

    /** The refresh token is rejected: the user must sign in again. */
    data object InvalidSession : RefreshResult

    /** Network/server hiccup: keep the session and fail only this request. */
    data object TransientFailure : RefreshResult
}
