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

/** Round trip with account, supervisor and lines; paging; Arabic search; voice note file; approval flow; upload flags. */
@RunWith(AndroidJUnit4::class)
class VisitRepoTest : RepoTestBase() {
    private val repo get() = VisitRepoImpl(db.visitDao(), media)

    @Test fun saveThenGetKeepsAccountSupervisorAndLines() = runTest {
        AccountRepoImpl(db.accountDao()).save(Account("a1", name = "Clinic", city = "Cairo"))
        RepRepoImpl(db.repDao()).save(Rep("s1", "Boss", role = RepRole.Supervisor))
        ProductRepoImpl(db.productDao(), db.custodyDao()).saveCustody(listOf(Custody("c1", "Pen", ProductType.Gift, received = 10)))
        repo.save(Visit(id = "v1", status = VisitStatus.Scheduled, accountId = "a1", supervisorId = "s1", gifts = listOf(Product(id = "c1", quantity = 2))))
        val visit = repo.get("v1")!!
        assertEquals("Clinic", visit.account)
        assertEquals("Cairo", visit.city)
        assertEquals("Boss", visit.supervisorName)
        assertEquals(2, visit.gifts?.single()?.quantity)
        assertNull(repo.get("none"))
    }

    @Test fun pagingGivesTwentyThenMore() = runTest {
        (1..60).forEach { repo.save(Visit(id = "v$it", status = VisitStatus.Scheduled, dateTimeMillis = it * 1000L)) }
        assertTrue(repo.visits().asSnapshot().size in 20..40)
        assertTrue(repo.visits().asSnapshot { scrollTo(index = 30) }.size > 20)
    }

    @Test fun searchFindsByAccountInAnyArabicForm() = runTest {
        AccountRepoImpl(db.accountDao()).save(Account("a1", name = "صيدلية الإيمان"))
        repo.save(Visit(id = "v1", status = VisitStatus.Scheduled, accountId = "a1"))
        repo.save(Visit(id = "v2", status = VisitStatus.Scheduled))
        assertEquals(listOf("v1"), repo.visits("صيدليه الايمان").asSnapshot().map { it.id })
        assertEquals(2, repo.visits("").asSnapshot().size)
        assertTrue(repo.visits("zzz").asSnapshot().isEmpty())
    }

    @Test fun voiceNoteIsSavedToAFileAndRemovedWithTheVisit() = runTest {
        repo.save(Visit(id = "v1", status = VisitStatus.Scheduled, voiceNote = byteArrayOf(1, 2, 3)))
        val path = repo.get("v1")!!.voiceNotePath!!
        assertArrayEquals(byteArrayOf(1, 2, 3), media.read(path))
        repo.delete("v1")
        assertNull(repo.get("v1"))
        assertNull(media.read(path))
    }

    @Test fun savingWithoutNewBytesKeepsTheOldVoiceNotePath() = runTest {
        repo.save(Visit(id = "v1", status = VisitStatus.Scheduled, voiceNote = byteArrayOf(1)))
        val path = repo.get("v1")!!.voiceNotePath
        repo.save(repo.get("v1")!!.copy(noIssueFound = false))
        assertEquals(path, repo.get("v1")!!.voiceNotePath)
    }

    @Test fun approvedRequestBecomesStartableAndRejectedCanBeDismissed() = runTest {
        repo.save(Visit(id = "p1", type = VisitType.Planning, status = VisitStatus.PendingApproval, backendId = "b1"))
        repo.save(Visit(id = "p2", type = VisitType.Planning, status = VisitStatus.PendingApproval, backendId = "b2"))
        assertEquals(2, repo.pendingCount().first())
        assertTrue(repo.applyDecision("b1", true, 9))
        assertTrue(repo.applyDecision("b2", false, 9))
        assertFalse(repo.applyDecision("none", true, 9))
        assertEquals(listOf("p1"), repo.startableVisits().asSnapshot().map { it.id })
        assertTrue(repo.pendingRequests().asSnapshot().isEmpty())
        assertEquals(listOf("p2"), repo.rejectedRequests().asSnapshot().map { it.id })
        assertTrue(repo.dismissRejected("p2"))
        assertFalse(repo.dismissRejected("p1"))
    }

    @Test fun requiredAndDoneListsAreFilteredInTheDatabase() = runTest {
        repo.save(Visit(id = "todo", type = VisitType.Planned, shift = Shift.Am, status = VisitStatus.Scheduled))
        repo.save(Visit(id = "done", type = VisitType.Planned, shift = Shift.Pm, status = VisitStatus.Completed, completedAtMillis = 5L))
        assertEquals(listOf("todo"), repo.requiredVisits().asSnapshot().map { it.id })
        assertEquals(listOf("done"), repo.doneVisits(shift = Shift.Pm).asSnapshot().map { it.id })
        assertEquals(0, repo.doneCount(shift = Shift.Am).first())
        assertEquals(1, repo.requiredCount().first())
    }

    @Test fun cancelOnlyBeforeUploadAndMarkSynced() = runTest {
        repo.save(Visit(id = "v1", status = VisitStatus.Scheduled, type = VisitType.Planning, syncStatus = SyncStatus.NotSynced))
        assertEquals(listOf("v1"), repo.notSynced().map { it.id })
        repo.markSynced("v1", "srv")
        assertTrue(repo.notSynced().isEmpty())
        assertEquals("srv", repo.get("v1")!!.backendId)
        assertFalse(repo.cancelRequest("v1"))
    }
}
