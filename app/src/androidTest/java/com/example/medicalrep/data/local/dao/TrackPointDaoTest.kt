package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.SyncStatus

/** CRUD, uniqueness per rep and time, upload flags, the supervisor cache and the 7 day purge. */
@RunWith(AndroidJUnit4::class)
class TrackPointDaoTest : DaoTestBase() {
    private val dao get() = db.trackPointDao()

    private suspend fun rep() = db.userDao().insert(user("r"))

    @Test fun insertGetDelete() = runBlocking {
        rep()
        val id = dao.insert(point("r", 100))
        assertEquals(100L, dao.getById(id)?.timeMillis)
        assertEquals(1, dao.deleteById(id))
        assertNull(dao.getById(id))
    }

    @Test fun samePointTwiceIsStoredOnce() = runBlocking {
        rep()
        dao.insertOrReplace(listOf(point("r", 100), point("r", 100), point("r", 200)))
        assertEquals(2, dao.getAll().first().size)
    }

    @Test fun byVisitAndByRepAndDayAreOrderedByTime() = runBlocking {
        rep()
        db.visitDao().save(visit("v", repId = "r"), emptyList())
        dao.insertAll(listOf(point("r", 300, "v"), point("r", 100, "v"), point("r", 900)))
        assertEquals(listOf(100L, 300L), dao.getByVisit("v").first().map { it.timeMillis })
        assertEquals(listOf(100L, 300L), dao.getByRepAndDay("r", 0, 500).first().map { it.timeMillis })
        assertTrue(dao.getByRepAndDay("other", 0, 500).first().isEmpty())
    }

    @Test fun getNotSyncedAndMarkSynced() = runBlocking {
        rep()
        val id = dao.insert(point("r", 100))
        dao.insert(point("r", 200, syncStatus = SyncStatus.Synced))
        assertEquals(listOf(100L), dao.getNotSynced().map { it.timeMillis })
        dao.markSynced(id, "srv")
        assertTrue(dao.getNotSynced().isEmpty())
        assertEquals("srv", dao.getById(id)?.backendId)
    }

    @Test fun purgeDeletesOnlyOldSyncedPoints() = runBlocking {
        rep()
        dao.insertAll(listOf(
            point("r", 100, syncStatus = SyncStatus.Synced),
            point("r", 200, syncStatus = SyncStatus.NotSynced),
            point("r", 900, syncStatus = SyncStatus.Synced),
        ))
        assertEquals(1, dao.purgeOlderThan(500))
        assertEquals(listOf(200L, 900L), dao.getAll().first().map { it.timeMillis })
    }

    @Test fun replaceForRepAndDayKeepsUnsentPoints() = runBlocking {
        rep()
        dao.insertAll(listOf(
            point("r", 100, syncStatus = SyncStatus.Synced),
            point("r", 150, syncStatus = SyncStatus.NotSynced),
            point("r", 900, syncStatus = SyncStatus.Synced),
        ))
        dao.replaceForRepAndDay("r", 0, 500, listOf(point("r", 120, syncStatus = SyncStatus.Synced)))
        assertEquals(listOf(120L, 150L, 900L), dao.getAll().first().map { it.timeMillis })
    }

    @Test fun deletingTheVisitDeletesItsPoints() = runBlocking {
        rep()
        db.visitDao().save(visit("v", repId = "r"), emptyList())
        dao.insert(point("r", 100, "v"))
        db.visitDao().deleteById("v")
        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test fun clearEmptiesTable() = runBlocking {
        rep()
        dao.insert(point("r", 1))
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }
}
