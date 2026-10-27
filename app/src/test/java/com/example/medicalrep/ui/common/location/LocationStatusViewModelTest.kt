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
import com.example.medicalrep.domain.models.lastLocation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.repo.FakeTrackPointRepo
import com.appcore.core.data.local.datastore.model.UserDS
import com.example.medicalrep.domain.repo.FakeSession
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
        viewModel = LocationStatusViewModel(FakeSettingsRepo, helper, trackPointRepo, session.manager)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val helper = FakeLocationHelper(LocationStatus.PermissionDenied)
    private val trackPointRepo = FakeTrackPointRepo()
    private val session = FakeSession(UserDS(id = 7))
    private val users = session.users
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
        assertEquals(GeoPoint(30.1, 31.1), session.manager.user.value.lastLocation)
        helper.updates.emit(GeoPoint(30.2, 31.2))
        assertEquals(GeoPoint(30.2, 31.2), session.manager.user.value.lastLocation)
        assertTrue(session.manager.user.value.lastLocationTimeMillis > 0)
        job.cancel()
        helper.updates.emit(GeoPoint(30.3, 31.3))
        assertEquals(GeoPoint(30.2, 31.2), session.manager.user.value.lastLocation)
    }

    @Test
    fun `every gps update is also stored as a track point of the current rep, not uploaded`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.trackLocation() }
        helper.updates.emit(GeoPoint(30.1, 31.1))
        helper.updates.emit(GeoPoint(30.2, 31.2))
        val points = trackPointRepo.added
        assertEquals(listOf(30.1 to 31.1, 30.2 to 31.2), points.map { it.latitude to it.longitude })
        assertTrue(points.all { it.repId == "7" })
        assertTrue(points.all { it.visitId == null && it.syncStatus == SyncStatus.NotSynced && it.timeMillis > 0 })
        assertEquals(points.last().timeMillis, session.manager.user.value.lastLocationTimeMillis)
        job.cancel()
    }

    @Test
    fun `without a signed in user no track point is stored but the last location still is`() = runTest {
        session.manager.updateUser { UserDS() } // nobody signed in
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.trackLocation() }
        helper.updates.emit(GeoPoint(30.1, 31.1))
        assertEquals(emptyList<Any>(), trackPointRepo.added)
        assertEquals(GeoPoint(30.1, 31.1), session.manager.user.value.lastLocation)
        job.cancel()
    }

    @Test
    fun `a failing track point insert does not stop saving the last location or later updates`() = runTest {
        trackPointRepo.failOnAdd = true
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.trackLocation() }
        helper.updates.emit(GeoPoint(30.1, 31.1))
        trackPointRepo.failOnAdd = false
        helper.updates.emit(GeoPoint(30.2, 31.2))
        assertEquals(GeoPoint(30.2, 31.2), session.manager.user.value.lastLocation)
        assertEquals(listOf(30.2), trackPointRepo.added.map { it.latitude })
        job.cancel()
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
