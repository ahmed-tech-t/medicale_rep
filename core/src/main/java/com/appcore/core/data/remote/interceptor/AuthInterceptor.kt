package com.appcore.core.data.remote.interceptor

import com.appcore.core.data.local.session.AuthSessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/** Sends `Authorization: Bearer <access>` when the user is signed in. */
@Singleton
class AuthInterceptor @Inject constructor(
    private val authSessionManager: AuthSessionManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val access = authSessionManager.getAccessTokenSync()
        val request = chain.request().newBuilder().apply {
            if (access.isNotBlank()) header("Authorization", "Bearer $access")
        }.build()
        return chain.proceed(request)
    }
}
