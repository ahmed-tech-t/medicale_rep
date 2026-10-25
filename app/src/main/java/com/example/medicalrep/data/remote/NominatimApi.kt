package com.example.medicalrep.data.remote

import com.example.medicalrep.data.remote.dto.NominatimReverseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("accept-language") language: String,
        @Query("format") format: String = "jsonv2",
    ): NominatimReverseDto
}
