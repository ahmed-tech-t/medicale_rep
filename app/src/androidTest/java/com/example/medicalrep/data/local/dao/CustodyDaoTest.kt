package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import com.example.medicalrep.data.local.search.normalizeSearch
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.SyncStatus

/** CRUD, paging, kind filter, search and the offline remaining balance. */
@RunWith(AndroidJUnit4::class)
class CustodyDaoTest : DaoTestBase() {
    private val dao get() = db.custodyDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(custody("c1", "Pen"))
        assertEquals("Pen", dao.getById("c1")?.name)
        dao.update(custody("c1", "Cap"))
        assertEquals("Cap", dao.getById("c1")?.name)
        assertEquals(1, dao.deleteById("c1"))
        assertNull(dao.getById("c1"))
    }

    @Test fun pagingAndKindFilter() = runBlocking {
        dao.insertAll((1..30).map { custody("g%02d".format(java.util.Locale.ROOT, it), "G%02d".format(java.util.Locale.ROOT, it), ProductType.Gift) })
        dao.insertAll((1..30).map { custody("s%02d".format(java.util.Locale.ROOT, it), "S%02d".format(java.util.Locale.ROOT, it), ProductType.Product) })
        assertEquals(20, dao.paged().firstPage().data.size)
        assertEquals(30, dao.pagedByKind(ProductType.Product).allRows().size)
        assertTrue(dao.pagedByKind(ProductType.Product).allRows().all { it.custody.kind == ProductType.Product })
    }

    @Test fun searchPartialAndArabic() = runBlocking {
        dao.insertAll(listOf(custody("1", "قلم أزرق"), custody("2", "Cap")))
        assertEquals(listOf("1"), dao.search(normalizeSearch("قلم ازرق")).allRows().map { it.custody.id })
        assertEquals(listOf("2"), dao.search("ca").allRows().map { it.custody.id })
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun remainingSubtractsQuantitiesInVisitsNotUploadedYet() = runBlocking {
        dao.insert(custody("c1", received = 10, dispatched = 2))
        db.visitDao().save(visit("pending", syncStatus = SyncStatus.NotSynced), listOf(VisitCustodyEntity("pending", "c1", 3)))
        db.visitDao().save(visit("uploaded", syncStatus = SyncStatus.Synced), listOf(VisitCustodyEntity("uploaded", "c1", 4)))
        val row = dao.getWithPending("c1")!!.toDomain()
        assertEquals(3, row.pending)
        assertEquals(5, row.remaining)
    }

    @Test fun noVisitsMeansNoPending() = runBlocking {
        dao.insert(custody("c1", received = 10, dispatched = 2))
        assertEquals(8, dao.getWithPending("c1")!!.toDomain().remaining)
        assertNull(dao.getWithPending("none"))
    }
}
