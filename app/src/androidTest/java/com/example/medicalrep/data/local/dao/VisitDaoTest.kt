package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import com.example.medicalrep.domain.models.UserRole
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType

/** CRUD, paging, search through the account, lines, filters, download rules and the plan approval flow. */
@RunWith(AndroidJUnit4::class)
class VisitDaoTest : DaoTestBase() {
    private val dao get() = db.visitDao()

    @Test fun saveGetUpdateDelete() = runBlocking {
        dao.save(visit("v1", backendId = "b"), emptyList())
        assertEquals("b", dao.getById("v1")?.backendId)
        dao.update(visit("v1", backendId = "c"))
        assertEquals("c", dao.getById("v1")?.backendId)
        assertEquals(1, dao.deleteById("v1"))
        assertNull(dao.getById("v1"))
    }

    @Test fun pagingIsNewestFirstTwentyPerPage() = runBlocking {
        (1..60).forEach { dao.save(visit("v%02d".format(java.util.Locale.ROOT, it), dateTimeMillis = it * 1000L), emptyList()) }
        val page = dao.paged().firstPage()
        assertEquals(20, page.data.size)
        assertEquals("v60", page.data.first().visit.id)
        assertNotNull(page.nextKey)
        assertNull(dao.paged().firstPage(100).nextKey)
    }

    @Test fun searchFindsVisitsByAccountTextInAnyArabicForm() = runBlocking {
        db.accountDao().save(account("a1", "صيدلية الإيمان", "Cairo"))
        db.accountDao().save(account("a2", "Other"))
        dao.save(visit("v1", accountId = "a1"), emptyList())
        dao.save(visit("v2", accountId = "a2"), emptyList())
        assertEquals(listOf("v1"), dao.search(normalizeSearch("صيدليه الايمان")).allRows().map { it.visit.id })
        assertEquals(listOf("v1"), dao.search("cairo").allRows().map { it.visit.id })
        assertEquals(2, dao.search("").allRows().size)
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun searchFindsVisitsByDate() = runBlocking {
        dao.save(visit("v1", dateTimeMillis = 1_700_000_000_000L), emptyList())
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT).format(java.util.Date(1_700_000_000_000L))
        assertEquals(1, dao.search(date).allRows().size)
    }

    @Test fun detailsCarryAccountSupervisorAndLines() = runBlocking {
        db.accountDao().save(account("a1", "Clinic"))
        db.userDao().save(user("s", "Boss", UserRole.Supervisor))
        db.custodyDao().insertAll(listOf(custody("c1", kind = ProductType.Gift), custody("c2", kind = ProductType.Product)))
        dao.save(visit("v1", accountId = "a1", supervisorId = "s"), listOf(VisitCustodyEntity("v1", "c1", 2), VisitCustodyEntity("v1", "c2", 1)))
        val details = dao.getWithDetails("v1")!!
        assertEquals("Clinic", details.account?.name)
        assertEquals("Boss", details.supervisor?.displayName)
        assertEquals(setOf("c1", "c2"), details.custody.map { it.id }.toSet())
        assertEquals(3, details.lines.sumOf { it.quantity })
    }

    @Test fun replaceLinesAndCascadeDelete() = runBlocking {
        db.custodyDao().insertAll(listOf(custody("c1"), custody("c2")))
        dao.save(visit("v1"), listOf(VisitCustodyEntity("v1", "c1", 2)))
        dao.replaceLines("v1", listOf(VisitCustodyEntity("v1", "c2", 5)))
        assertEquals(listOf("c2"), dao.getWithDetails("v1")!!.lines.map { it.custodyId })
        dao.deleteById("v1")
        assertTrue(dao.getWithDetails("v1") == null)
    }

    @Test fun replacingACustodyRowKeepsVisitLines() = runBlocking {
        db.custodyDao().upsert(custody("c1", received = 10))
        dao.save(visit("v1"), listOf(VisitCustodyEntity("v1", "c1", 2)))
        db.custodyDao().upsert(custody("c1", received = 20))
        assertEquals(1, dao.getWithDetails("v1")!!.lines.size)
    }

    @Test fun filtersAreOptional() = runBlocking {
        dao.save(visit("v1", type = VisitType.Planned, repId = null, dateTimeMillis = 100), emptyList())
        dao.save(visit("v2", type = VisitType.Unplanned, syncStatus = SyncStatus.NotSynced, dateTimeMillis = 200), emptyList())
        assertEquals(2, dao.pagedFiltered(null, null, null, 0, 1000).allRows().size)
        assertEquals(listOf("v1"), dao.pagedFiltered(VisitType.Planned, null, null, 0, 1000).allRows().map { it.visit.id })
        assertEquals(listOf("v2"), dao.pagedFiltered(null, SyncStatus.NotSynced, null, 0, 1000).allRows().map { it.visit.id })
        assertEquals(listOf("v2"), dao.pagedFiltered(null, null, null, 150, 1000).allRows().map { it.visit.id })
    }

    @Test fun notSyncedMarkSyncedAndUpsertFromServer() = runBlocking {
        dao.save(visit("v1", syncStatus = SyncStatus.NotSynced, backendId = null), emptyList())
        dao.save(visit("v2", backendId = "old"), emptyList())
        assertEquals(listOf("v1"), dao.getNotSynced().map { it.id })
        dao.upsertFromServer(listOf(visit("v1", backendId = "srv"), visit("v2", backendId = "new"), visit("v3", backendId = "n3")))
        assertNull(dao.getById("v1")?.backendId)
        assertEquals("new", dao.getById("v2")?.backendId)
        assertEquals("n3", dao.getById("v3")?.backendId)
        dao.markSynced("v1", "srv")
        assertTrue(dao.getNotSynced().isEmpty())
        assertEquals("srv", dao.getById("v1")?.backendId)
    }

    @Test fun planRequestIsPendingThenApprovedBecomesStartable() = runBlocking {
        dao.save(visit("p", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, requestedAtMillis = 5, backendId = "b1"), emptyList())
        assertEquals(1, dao.countPending().first())
        assertEquals(listOf("p"), dao.pendingPaged().allRows().map { it.visit.id })
        assertTrue(dao.startablePaged().allRows().isEmpty())
        assertEquals(1, dao.applyDecision("b1", ApprovalStatus.Approved, null, 9))
        assertEquals(0, dao.countPending().first())
        assertTrue(dao.pendingPaged().allRows().isEmpty())
        val started = dao.startablePaged().allRows().single().visit
        assertEquals(VisitType.Planned, started.type)
        assertEquals(9L, started.decidedAtMillis)
    }

    @Test fun rejectedRequestKeepsReasonUntilDismissed() = runBlocking {
        dao.save(visit("p", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, backendId = "b1"), emptyList())
        dao.applyDecision("b1", ApprovalStatus.Rejected, "too far", 9)
        val row = dao.pendingPaged().allRows().single().visit
        assertEquals(VisitType.Planning, row.type)
        assertEquals("too far", row.decisionNote)
        assertTrue(dao.startablePaged().allRows().isEmpty())
        assertEquals(1, dao.dismissRejected("p"))
        assertNull(dao.getById("p"))
    }

    @Test fun unknownBackendIdChangesNothingAndDismissIgnoresPending() = runBlocking {
        dao.save(visit("p", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, backendId = "b1"), emptyList())
        assertEquals(0, dao.applyDecision("nope", ApprovalStatus.Approved, null, 9))
        assertEquals(0, dao.dismissRejected("p"))
        assertNotNull(dao.getById("p"))
    }

    @Test fun cancelOnlyWorksOnRequestsNotUploadedYet() = runBlocking {
        dao.save(visit("local", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, syncStatus = SyncStatus.NotSynced), emptyList())
        dao.save(visit("sent", type = VisitType.Planning, approvalStatus = ApprovalStatus.Pending, syncStatus = SyncStatus.Synced), emptyList())
        assertEquals(1, dao.cancelRequest("local"))
        assertEquals(0, dao.cancelRequest("sent"))
    }

    @Test fun clearEmptiesTable() = runBlocking {
        dao.save(visit("v1"), emptyList())
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }
}
