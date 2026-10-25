package com.example.medicalrep.data.di

import com.example.medicalrep.BuildConfig
import com.example.medicalrep.data.remote.OsrmApi
import com.example.medicalrep.data.repo.RouteRepoImpl
import com.example.medicalrep.domain.repo.RouteRepo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/** OSRM routing server; 10.0.2.2 is the host machine as seen from the Android emulator. */
private const val OSRM_BASE_URL = "http://10.0.2.2:5000/"

@Module
@InstallIn(SingletonComponent::class)
abstract class RouteModule {

    @Binds
    abstract fun bindRouteRepo(impl: RouteRepoImpl): RouteRepo

    companion object {
        // Own client without the auth/headers interceptors, so the user's token is never sent to OSRM.
        @Provides
        @Singleton
        fun provideOsrmApi(json: Json, loggingInterceptor: HttpLoggingInterceptor): OsrmApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .apply { if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor) }
                .build()
            return Retrofit.Builder()
                .baseUrl(OSRM_BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(OsrmApi::class.java)
        }
    }
}
