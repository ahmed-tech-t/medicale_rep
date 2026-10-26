package com.example.medicalrep.ui.common.location

import com.appcore.core.domain.models.AppSettings
import com.appcore.core.domain.repo.AppSettingsRepo
import com.example.medicalrep.domain.models.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import com.example.medicalrep.domain.repo.FakeLastLocationRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 1. Given the helper reports a status, When the screen opens, Then the state has that status.
 * 2. Given the status changes (permission granted, GPS toggled), When Refresh, Then the state follows it.
 * 4. While tracking, each GPS update is saved as the last location; cancelling stops saving.
 * 3. Given the app stays open, When the helper reports a system change (GPS switched off), Then the state follows it
 *    without a Refresh.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocationStatusViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LocationStatusViewModel(FakeSettingsRepo, helper, lastLocationRepo)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val helper = FakeLocationHelper(LocationStatus.PermissionDenied)
    private val lastLocationRepo = FakeLastLocationRepo()
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
    fun `tracking saves every gps update and stops when cancelled`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.trackLocation() }
        helper.updates.emit(GeoPoint(30.1, 31.1))
        helper.updates.emit(GeoPoint(30.2, 31.2))
        assertEquals(listOf(GeoPoint(30.1, 31.1), GeoPoint(30.2, 31.2)), lastLocationRepo.saved.map { it.first })
        assertTrue(lastLocationRepo.saved.all { it.second > 0 })
        job.cancel()
        helper.updates.emit(GeoPoint(30.3, 31.3))
        assertEquals(2, lastLocationRepo.saved.size)
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
    val updates = MutableSharedFlow<GeoPoint>(extraBufferCapacity = 8)
    override fun updates(): Flow<GeoPoint> = updates
}

private object FakeSettingsRepo : AppSettingsRepo {
    override fun getSettings(): Flow<AppSettings> = emptyFlow()
    override suspend fun updateLanguage(language: String) = Unit
    override suspend fun updateCountry(country: String) = Unit
    override suspend fun getShowOnBoarding() = false
    override suspend fun updateShowOnBoarding(showOnBoarding: Boolean) = Unit
}
