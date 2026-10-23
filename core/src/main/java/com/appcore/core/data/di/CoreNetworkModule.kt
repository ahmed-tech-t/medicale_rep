package com.appcore.core.data.di

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.appcore.core.config.CoreConfig
import com.appcore.core.data.di.annotation.AppInterceptor
import com.appcore.core.data.di.annotation.RefreshClient
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.data.remote.interceptor.AuthInterceptor
import com.appcore.core.data.remote.interceptor.ClientHeadersInterceptor
import com.appcore.core.data.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Provides `Json`, the main `OkHttpClient`/`Retrofit` (auth + headers + token refresh) and the
 * `@RefreshClient` pair. The app still creates its own Retrofit services from these.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class CoreNetworkModule {

    /** Lets apps add zero or more interceptors with `@IntoSet @AppInterceptor`. */
    @Multibinds
    @AppInterceptor
    abstract fun appInterceptors(): Set<Interceptor>

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        @Provides
        @Singleton
        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            @ApplicationContext context: Context,
            config: CoreConfig,
            appSettingsManager: AppSettingsManager,
            authInterceptor: AuthInterceptor,
            tokenAuthenticator: TokenAuthenticator,
            @AppInterceptor appInterceptors: Set<@JvmSuppressWildcards Interceptor>,
            httpLoggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            @SuppressLint("HardwareIds")
            val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

            val builder = baseClientBuilder()
                .addInterceptor(authInterceptor)
                .addInterceptor(ClientHeadersInterceptor(appSettingsManager, config, deviceId))
                .authenticator(tokenAuthenticator)
            appInterceptors.forEach { builder.addInterceptor(it) }
            if (config.isDebug) builder.addDebugInterceptors(context, httpLoggingInterceptor)
            return builder.build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient, json: Json, config: CoreConfig): Retrofit =
            buildRetrofit(okHttpClient, json, config)

        @Provides
        @Singleton
        @RefreshClient
        fun provideRefreshOkHttpClient(
            @ApplicationContext context: Context,
            config: CoreConfig,
            httpLoggingInterceptor: HttpLoggingInterceptor,
        ): OkHttpClient {
            val builder = baseClientBuilder()
            if (config.isDebug) builder.addDebugInterceptors(context, httpLoggingInterceptor)
            return builder.build()
        }

        @Provides
        @Singleton
        @RefreshClient
        fun provideRefreshRetrofit(
            @RefreshClient okHttpClient: OkHttpClient,
            json: Json,
            config: CoreConfig,
        ): Retrofit = buildRetrofit(okHttpClient, json, config)

        private fun baseClientBuilder() = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)

        private fun OkHttpClient.Builder.addDebugInterceptors(
            context: Context,
            httpLoggingInterceptor: HttpLoggingInterceptor,
        ) {
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(ChuckerInterceptor.Builder(context).build())
        }

        private fun buildRetrofit(okHttpClient: OkHttpClient, json: Json, config: CoreConfig) =
            Retrofit.Builder()
                .baseUrl(config.baseUrl)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .client(okHttpClient)
                .build()
    }
}
