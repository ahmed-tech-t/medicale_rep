package com.example.medicalrep.data.di

import com.example.medicalrep.BuildConfig
import com.example.medicalrep.data.remote.NominatimApi
import com.example.medicalrep.data.repo.GeocodingRepoImpl
import com.example.medicalrep.domain.repo.GeocodingRepo
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

@Module
@InstallIn(SingletonComponent::class)
abstract class GeocodingModule {

    @Binds
    abstract fun bindGeocodingRepo(impl: GeocodingRepoImpl): GeocodingRepo

    companion object {
        // The public Nominatim allows 1 request/second and needs a User-Agent; self-host it before heavy use.
        // Own client without the auth/headers interceptors, so the user's token is never sent to Nominatim.
        @Provides
        @Singleton
        fun provideNominatimApi(json: Json, loggingInterceptor: HttpLoggingInterceptor): NominatimApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request().newBuilder()
                            .header("User-Agent", "MedicalRep/${BuildConfig.VERSION_NAME}")
                            .build()
                    )
                }
                .apply { if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor) }
                .build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.NOMINATIM_BASE_URL)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(NominatimApi::class.java)
        }
    }
}
