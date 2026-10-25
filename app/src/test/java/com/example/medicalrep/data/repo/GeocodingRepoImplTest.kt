package com.example.medicalrep.data.repo

import com.appcore.core.config.CoreConfig
import com.appcore.core.data.local.settings.AppSettingsManager
import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.appcore.core.domain.utils.Resource
import com.example.medicalrep.data.remote.NominatimApi
import com.example.medicalrep.data.remote.dto.NominatimReverseDto
import com.example.medicalrep.domain.models.RoutePoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * 1. Given Nominatim returns an address, Then Loading then Success with display_name.
 * 2. The request carries the point's latitude and longitude.
 * 3. Given Nominatim returns an error body or a blank address, Then Error.
 * 4. Given the call throws, Then Error.
 * 5. The request asks for the app language.
 */
class GeocodingRepoImplTest {

    private val point = RoutePoint(latitude = 30.0444, longitude = 31.2357)

    private val settings = AppSettingsManager(
        FakeSettingsRepo,
        CoreConfig(baseUrl = "", isDebug = false, cryptoKeyAlias = "", defaultLanguage = "ar"),
    )

    private fun repo(api: NominatimApi) = GeocodingRepoImpl(api, settings, Json)

    private class FakeApi(private val result: () -> NominatimReverseDto) : NominatimApi {
        var lastLat: Double? = null
        var lastLon: Double? = null
        var lastLanguage: String? = null
        override suspend fun reverse(lat: Double, lon: Double, language: String, format: String): NominatimReverseDto {
            lastLat = lat
            lastLon = lon
            lastLanguage = language
            return result()
        }
    }

    @Test
    fun `success returns display name and sends lat lon`() = runTest {
        val api = FakeApi { NominatimReverseDto(displayName = "Tahrir Square, Cairo, Egypt") }
        val emissions = repo(api).getAddress(point).toList()

        assertTrue(emissions[0] is Resource.Loading)
        assertEquals("Tahrir Square, Cairo, Egypt", (emissions[1] as Resource.Success).data)
        assertEquals(30.0444, api.lastLat)
        assertEquals(31.2357, api.lastLon)
        assertEquals("ar", api.lastLanguage)
    }

    @Test
    fun `error body and blank address are errors`() = runTest {
        listOf(
            NominatimReverseDto(error = "Unable to geocode"),
            NominatimReverseDto(),
            NominatimReverseDto(displayName = "  "),
        ).forEach { response ->
            val last = repo(FakeApi { response }).getAddress(point).toList().last()
            assertTrue(last is Resource.Error)
        }
    }

    @Test
    fun `exception becomes an error`() = runTest {
        val last = repo(FakeApi { throw IOException("boom") }).getAddress(point).toList().last()
        assertTrue(last is Resource.Error)
    }
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
