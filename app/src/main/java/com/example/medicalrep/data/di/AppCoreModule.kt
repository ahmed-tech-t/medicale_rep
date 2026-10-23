package com.example.medicalrep.data.di

import com.appcore.core.config.CoreConfig
import com.example.medicalrep.BuildConfig
import com.appcore.core.data.remote.interceptor.RefreshResult
import com.appcore.core.data.remote.interceptor.TokenRefresher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppCoreModule {

    @Provides
    @Singleton
    fun provideCoreConfig() = CoreConfig(
        baseUrl = "https://your-api.com/api/",
        isDebug = BuildConfig.DEBUG,
        cryptoKeyAlias = "my_app_aes_key", // Keystore alias that encrypts the saved session
        defaultLanguage = "ar",
    )

    // Call your refresh-token endpoint here, using the @RefreshClient Retrofit.
    @Provides
    fun provideTokenRefresher(): TokenRefresher = TokenRefresher { refreshToken ->
        RefreshResult.InvalidSession
    }
}