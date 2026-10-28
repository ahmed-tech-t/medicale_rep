package com.example.medicalrep.data.repo

import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.dao.DaoTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.*

/** Track points: one per rep and time, supervisor cache stored as synced, purge keeps unsent. Sync info: set replaces. */
@RunWith(AndroidJUnit4::class)
class TrackPointAndSyncInfoRepoTest : RepoTestBase() {
    private val points get() = TrackPointRepoImpl(db.trackPointDao())
    private val info get() = SyncInfoRepoImpl(db.syncInfoDao())

    private fun point(time: Long, status: SyncStatus = SyncStatus.NotSynced) = TrackPoint(repId = "r", latitude = 1.0, longitude = 2.0, timeMillis = time, syncStatus = status)

    @Test fun samePointTwiceIsStoredOnce() = runTest {
        points.add(point(100))
        points.add(point(100))
        points.add(point(200))
        assertEquals(listOf(100L, 200L), points.byRepAndDay("r", 0, 500).first().map { it.timeMillis })
    }

    @Test fun supervisorCacheIsStoredSyncedAndKeepsUnsentPoints() = runTest {
        points.add(point(150))
        points.replaceForRepAndDay("r", 0, 500, listOf(point(120), point(130)))
        assertEquals(listOf(150L), points.notSynced().map { it.timeMillis })
        assertEquals(3, points.byRepAndDay("r", 0, 500).first().size)
    }

    @Test fun purgeDeletesOnlyOldUploadedPoints() = runTest {
        points.add(point(100, SyncStatus.Synced))
        points.add(point(150))
        points.add(point(900, SyncStatus.Synced))
        assertEquals(1, points.purgeOlderThan(500))
        assertEquals(listOf(150L, 900L), points.byRepAndDay("r", 0, 1000).first().map { it.timeMillis })
    }

    @Test fun markSyncedRemovesFromNotSynced() = runTest {
        points.add(point(100))
        val id = points.notSynced().single().id
        points.markSynced(id, "srv")
        assertTrue(points.notSynced().isEmpty())
    }

    @Test fun syncInfoSetReplacesTheTime() = runTest {
        info.set("products", 1)
        info.set("products", 2)
        info.set("accounts", 3)
        assertEquals(2L, info.get("products")?.lastUpdatedMillis)
        assertEquals(2, info.all().first().size)
        assertNull(info.get("none"))
    }
}
