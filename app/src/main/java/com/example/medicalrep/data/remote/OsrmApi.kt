package com.example.medicalrep.data.remote

import com.example.medicalrep.data.remote.dto.OsrmRouteDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsrmApi {
    /** [coordinates] is `fromLon,fromLat;toLon,toLat`. */
    @GET("route/v1/driving/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates", encoded = true) coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson",
    ): OsrmRouteDto
}
