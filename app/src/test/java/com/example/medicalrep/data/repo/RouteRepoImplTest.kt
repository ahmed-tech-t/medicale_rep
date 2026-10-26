package com.example.medicalrep.data.repo

import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.data.remote.OsrmApi
import com.example.medicalrep.data.remote.dto.OsrmRouteDto
import com.example.medicalrep.domain.models.RoutePoint
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * 1. Given OSRM returns Ok with a line, Then Loading then Success with points as (lat, lon) from [lon, lat], plus the rounded distance (m) and duration (s).
 * 2. The request uses "lon,lat;lon,lat;..." for every waypoint, in order.
 * 3. Given code != Ok, or no routes, or an empty line, Then Error.
 * 4. Given the call throws, Then Error.
 */
class RouteRepoImplTest {

    private val from = RoutePoint(latitude = 30.0, longitude = 31.0)
    private val to = RoutePoint(latitude = 30.5, longitude = 31.5)

    private fun repo(api: OsrmApi) = RouteRepoImpl(api, Json)

    private fun dto(code: String = "Ok", line: List<List<Double>>? = listOf(listOf(31.0, 30.0), listOf(31.5, 30.5))) =
        OsrmRouteDto(
            code = code,
            routes = line?.let { listOf(OsrmRouteDto.OsrmRoute(OsrmRouteDto.Geometry(it), distance = 1234.4, duration = 567.8)) } ?: emptyList(),
        )

    private class FakeApi(private val result: () -> OsrmRouteDto) : OsrmApi {
        var lastCoordinates: String? = null
        override suspend fun getRoute(coordinates: String, overview: String, geometries: String): OsrmRouteDto {
            lastCoordinates = coordinates
            return result()
        }
    }

    @Test
    fun `success maps lon lat to lat lon and sends lon lat order`() = runTest {
        val api = FakeApi { dto() }
        val emissions = repo(api).getRoute(listOf(from, to)).toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertEquals(
            listOf(RoutePoint(30.0, 31.0), RoutePoint(30.5, 31.5)),
            (emissions[1] as Resource.Success).data?.points,
        )
        assertEquals(1234, (emissions[1] as Resource.Success).data?.distanceMeters)
        assertEquals(568, (emissions[1] as Resource.Success).data?.durationSeconds)
        assertEquals("31.0,30.0;31.5,30.5", api.lastCoordinates)
    }

    @Test
    fun `three waypoints are sent in order`() = runTest {
        val api = FakeApi { dto() }
        repo(api).getRoute(listOf(from, RoutePoint(30.2, 31.2), to)).toList()
        assertEquals("31.0,30.0;31.2,30.2;31.5,30.5", api.lastCoordinates)
    }

    @Test
    fun `non Ok code, missing routes and empty line are errors`() = runTest {
        listOf(dto(code = "NoRoute"), dto(line = null), dto(line = emptyList())).forEach { response ->
            val last = repo(FakeApi { response }).getRoute(listOf(from, to)).toList().last()
            assertTrue(last is Resource.Error)
        }
    }

    @Test
    fun `exception becomes an error`() = runTest {
        val last = repo(FakeApi { throw IOException("boom") }).getRoute(listOf(from, to)).toList().last()
        assertTrue(last is Resource.Error)
    }
}
