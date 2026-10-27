package com.example.medicalrep.domain.models

import com.appcore.core.data.local.datastore.model.UserDS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The last location lives in the user: a point only when both numbers exist; withLocation sets the point, the accuracy and the time. */
class UserLocationTest {

    @Test
    fun `no saved numbers give no location`() {
        assertNull(UserDS().lastLocation)
        assertNull(UserDS(lastLatitude = 30.5).lastLocation)
        assertNull(UserDS(lastLongitude = 31.1).lastLocation)
    }

    @Test
    fun `both numbers give a point`() {
        assertEquals(GeoPoint(30.5, 31.1), UserDS(lastLatitude = 30.5, lastLongitude = 31.1).lastLocation)
    }

    @Test
    fun `with location sets the point and the time and keeps the rest`() {
        val user = UserDS(id = 7, firstName = "Ahmed").withLocation(LocationFix(GeoPoint(30.5, 31.1), 12f), timeMillis = 99L)
        assertEquals(GeoPoint(30.5, 31.1), user.lastLocation)
        assertEquals(99L, user.lastLocationTimeMillis)
        assertEquals(12f, user.lastAccuracyMeters)
        assertEquals(7, user.id)
        assertEquals("Ahmed", user.firstName)
    }
}
