package com.example.medicalrep.data.repo

import com.appcore.core.data.local.datastore.model.UserDS
import com.example.medicalrep.domain.models.GeoPoint
import com.example.medicalrep.domain.repo.FakeUserLocalRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 1. Nothing saved: no location. 2. Save then read it back, with its time kept in the user.
 * 3. A second save replaces the first. 4. Clearing the user clears the location.
 * 5. A user saved before this feature (no location fields) reads as no location.
 */
class LastLocationRepoImplTest {
    private val users = FakeUserLocalRepo(UserDS())
    private val repo = LastLocationRepoImpl(users)

    @Test
    fun `nothing saved gives no location`() = runTest {
        assertNull(repo.current())
        assertNull(repo.lastLocation.first())
    }

    @Test
    fun `a saved location is read back and its time is kept`() = runTest {
        repo.save(GeoPoint(30.5, 31.1), timeMillis = 1234L)
        assertEquals(GeoPoint(30.5, 31.1), repo.current())
        assertEquals(1234L, users.current.value.lastLocationTimeMillis)
    }

    @Test
    fun `a second save replaces the first`() = runTest {
        repo.save(GeoPoint(30.5, 31.1), 1L)
        repo.save(GeoPoint(30.6, 31.2), 2L)
        assertEquals(GeoPoint(30.6, 31.2), repo.lastLocation.first())
    }

    @Test
    fun `clearing the user clears the location`() = runTest {
        repo.save(GeoPoint(30.5, 31.1), 1L)
        users.clear()
        assertNull(repo.current())
    }

    @Test
    fun `saving keeps the rest of the user`() = runTest {
        users.saveUser(UserDS(firstName = "Ahmed"))
        repo.save(GeoPoint(30.5, 31.1), 1L)
        assertEquals("Ahmed", users.current.value.firstName)
    }

    @Test
    fun `a user without a saved location reads as none`() = runTest {
        users.saveUser(UserDS(firstName = "Ahmed", lastLatitude = null, lastLongitude = null))
        assertNull(repo.current())
    }
}
