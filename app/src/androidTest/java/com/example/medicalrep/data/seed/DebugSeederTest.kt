package com.example.medicalrep.data.seed

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.repo.AccountRepoImpl
import com.example.medicalrep.data.repo.DoctorRepoImpl
import com.example.medicalrep.data.repo.DraftVisitRepoImpl
import com.example.medicalrep.data.repo.OrderRepoImpl
import com.example.medicalrep.data.repo.ProductRepoImpl
import com.example.medicalrep.data.repo.RepoTestBase
import com.example.medicalrep.data.repo.SyncInfoRepoImpl
import com.example.medicalrep.data.repo.RepRepoImpl
import com.example.medicalrep.data.repo.TrackPointRepoImpl
import com.example.medicalrep.data.repo.VisitRepoImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/** The seed fills an empty database once: every list is stored, a second run changes nothing. */
@RunWith(AndroidJUnit4::class)
class DebugSeederTest : RepoTestBase() {

    private val accounts get() = AccountRepoImpl(db.accountDao())
    private val syncInfo get() = SyncInfoRepoImpl(db.syncInfoDao())
    private val seeder
        get() = DebugSeeder(
            RepRepoImpl(db.repDao()),
            accounts,
            DoctorRepoImpl(db.doctorDao()),
            ProductRepoImpl(db.productDao(), db.custodyDao()),
            VisitRepoImpl(db.visitDao(), media),
            OrderRepoImpl(db.orderDao()),
            TrackPointRepoImpl(db.trackPointDao()),
            syncInfo,
        )

    @Test fun seedsAnEmptyDatabaseOnce() = runTest {
        assertNull(syncInfo.get(DebugSeeder.SEED_KEY))
        seeder.seedIfEmpty(now = 1_792_000_000_000L)
        assertNotNull(syncInfo.get(DebugSeeder.SEED_KEY))
        val expected = SeedDataFactory.build(1_792_000_000_000L)
        assertEquals(expected.visits.size, db.visitDao().getAll().first().size)
        assertEquals(expected.trackPoints.size, db.trackPointDao().getByRepAndDay("rep-1", 0, Long.MAX_VALUE).first().size + db.trackPointDao().getByRepAndDay("rep-2", 0, Long.MAX_VALUE).first().size + db.trackPointDao().getByRepAndDay("rep-3", 0, Long.MAX_VALUE).first().size)
        assertEquals(expected.orders.map { it.number }, db.orderDao().getAll().first().map { it.order.number }.sorted())
        assertEquals(expected.accounts.mapNotNull { it.city?.name }.toSet().sorted(), accounts.cities().first().map { it.name })
        seeder.seedIfEmpty(now = 1_792_000_000_000L + 1)
        assertEquals(expected.visits.size, db.visitDao().getAll().first().size)
    }
}
