package com.appcore.core.data.remote.interceptor

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.settings.AppSettingsManager
import okhttp3.Interceptor
import okhttp3.Response

/** Adds the JSON accept header, the app's static headers, and language/country/device-id. */
class ClientHeadersInterceptor(
    private val appSettingsManager: AppSettingsManager,
    private val config: CoreConfig,
    private val deviceId: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            addHeader("Accept", "application/json")
            config.staticHeaders.forEach { (name, value) -> addHeader(name, value) }
            config.languageHeader?.let { addHeader(it, appSettingsManager.getLanguageSync()) }
            config.deviceIdHeader?.let { addHeader(it, deviceId) }
        }.build()
        return chain.proceed(request)
    }
}
