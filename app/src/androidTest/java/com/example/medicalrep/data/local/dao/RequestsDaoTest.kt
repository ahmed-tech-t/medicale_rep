package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Notification
import com.example.medicalrep.domain.models.RepRequest
import com.example.medicalrep.domain.models.RequestCategory
import com.example.medicalrep.domain.models.RequestStatus
import com.example.medicalrep.domain.models.SystemSetting
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Requests, notifications and settings.
 *
 * Given requests of three categories, then a rep sees only their own, newest day first.
 * Given a pending request, when it is decided, then it is approved or rejected once; a second decision changes nothing.
 * Given notifications, when one is marked read, then the unread count drops; an unknown id changes nothing.
 * Given a rep is deleted, then their requests and notifications go.
 * Given an unknown setting key, then it reads null; setting it twice keeps the last value.
 */
@RunWith(AndroidJUnit4::class)
class RequestsDaoTest : DaoTestBase() {

    private fun request(id: String, repId: String = "r1", day: Long = 1, category: RequestCategory = RequestCategory.Leave) =
        RepRequest(id = id, repId = repId, dateMillis = day, category = category).toEntity()

    private fun notification(id: String, repId: String = "r1", at: Long = 1) =
        Notification(id = id, repId = repId, title = id, createdAtMillis = at).toEntity()

    @Test fun aRepSeesOnlyTheirRequestsNewestDayFirst() = runBlocking {
        db.repDao().save(rep("r1"))
        db.repDao().save(rep("r2"))
        db.requestDao().insertAll(listOf(
            request("a", day = 1, category = RequestCategory.OfficeWork),
            request("b", day = 3, category = RequestCategory.VisitPlan),
            request("c", day = 2),
            request("other", repId = "r2"),
        ))
        assertEquals(listOf("b", "c", "a"), db.requestDao().ofRep("r1").first().map { it.id })
    }

    @Test fun aPendingRequestIsDecidedOnce() = runBlocking {
        db.repDao().save(rep("r1"))
        db.requestDao().insert(request("a"))
        assertEquals(1, db.requestDao().decide("a", approved = true, decidedAtMillis = 50))
        assertEquals(0, db.requestDao().decide("a", approved = false, decidedAtMillis = 60))
        val stored = db.requestDao().ofRep("r1").first().single()
        assertEquals(RequestStatus.Approved, stored.status)
        assertEquals(50L, stored.decidedAtMillis)
    }

    @Test fun markingReadDropsTheUnreadCount() = runBlocking {
        db.repDao().save(rep("r1"))
        db.notificationDao().insertAll(listOf(notification("n1", at = 1), notification("n2", at = 2)))
        assertEquals(2, db.notificationDao().unreadCount("r1").first())
        assertEquals(1, db.notificationDao().markRead("n1"))
        assertEquals(0, db.notificationDao().markRead("none"))
        assertEquals(1, db.notificationDao().unreadCount("r1").first())
        assertEquals(listOf("n2", "n1"), db.notificationDao().ofRep("r1").first().map { it.id })
    }

    @Test fun deletingARepRemovesTheirRequestsAndNotifications() = runBlocking {
        db.repDao().save(rep("r1"))
        db.requestDao().insert(request("a"))
        db.notificationDao().insert(notification("n1"))
        db.repDao().deleteById("r1")
        assertTrue(db.requestDao().ofRep("r1").first().isEmpty())
        assertTrue(db.notificationDao().ofRep("r1").first().isEmpty())
    }

    @Test fun anUnknownSettingReadsNullAndTheLastValueWins() = runBlocking {
        assertNull(db.systemSettingDao().get("planning_days").first())
        db.systemSettingDao().upsert(SystemSetting("planning_days", "7").toEntity())
        db.systemSettingDao().upsert(SystemSetting("planning_days", "14").toEntity())
        assertEquals("14", db.systemSettingDao().get("planning_days").first())
    }
}
