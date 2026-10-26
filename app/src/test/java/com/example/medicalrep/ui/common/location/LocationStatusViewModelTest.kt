package com.example.medicalrep.ui.common.location

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.domain.models.map.MapCameraTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 1. Given the helper reports a status, When the screen opens, Then the state has that status.
 * 2. Given the status changes (permission granted, GPS toggled), When Refresh, Then the state follows it.
 * 3. Given the app stays open, When the helper reports a system change (GPS switched off), Then the state follows it
 *    without a Refresh.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocationStatusViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LocationStatusViewModel(FakeSettingsRepo, helper)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val helper = FakeLocationHelper(LocationStatus.PermissionDenied)
    private lateinit var viewModel: LocationStatusViewModel

    @Test
    fun `starts with the helper status`() {
        assertEquals(LocationStatus.PermissionDenied, viewModel.status.value)
    }

    @Test
    fun `a system change updates the status without refresh`() {
        helper.status = LocationStatus.GpsOff
        helper.changes.tryEmit(Unit)
        assertEquals(LocationStatus.GpsOff, viewModel.status.value)
    }

    @Test
    fun `refresh picks up status changes`() {
        helper.status = LocationStatus.GpsOff
        assertEquals(LocationStatus.PermissionDenied, viewModel.status.value)

        viewModel.onEvent(LocationStatusEvents.Refresh)
        assertEquals(LocationStatus.GpsOff, viewModel.status.value)

        helper.status = LocationStatus.Ready
        viewModel.onEvent(LocationStatusEvents.Refresh)
        assertEquals(LocationStatus.Ready, viewModel.status.value)
    }
}

private class FakeLocationHelper(var status: LocationStatus) : LocationHelper {
    val changes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override fun status() = status
    override fun statusChanges(): Flow<Unit> = changes
    override suspend fun getLocation(): MapCameraTarget? = null
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
