package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import com.example.medicalrep.domain.models.RepRole
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
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
        db.repDao().save(rep("s", "Boss", RepRole.Supervisor))
        db.catalogDao().insertAll(listOf(custody("c1", kind = ProductType.Gift), custody("c2", kind = ProductType.Sample)))
        dao.save(visit("v1", accountId = "a1", supervisorId = "s"), listOf(VisitItemEntity("v1", "c1", 2), VisitItemEntity("v1", "c2", 1)))
        val details = dao.getWithDetails("v1")!!
        assertEquals("Clinic", details.account?.account?.name)
        assertEquals("Boss", details.supervisor?.displayName)
        assertEquals(setOf("c1", "c2"), details.items.map { it.id }.toSet())
        assertEquals(3, details.lines.sumOf { it.quantity })
    }

    @Test fun doctorsSeenAreSavedReplacedAndLinkedOnly() = runBlocking {
        db.accountDao().save(account("a1"))
        db.addDoctor("a1", id = "d1")
        db.addDoctor("a1", id = "d2")
        dao.save(visit("v1", accountId = "a1"), emptyList(), listOf(VisitDoctorEntity("v1", "d1"), VisitDoctorEntity("v1", "d2")))
        assertEquals(setOf("d1", "d2"), dao.getWithDetails("v1")!!.doctors.map { it.id }.toSet())
        dao.save(visit("v1", accountId = "a1"), emptyList(), listOf(VisitDoctorEntity("v1", "d2")))
        assertEquals(listOf("d2"), dao.getWithDetails("v1")!!.doctors.map { it.id })
        db.doctorDao().delete(db.doctorDao().getById("d2")!!)
        assertTrue(dao.getWithDetails("v1")!!.doctors.isEmpty())
        assertEquals("v1", dao.getWithDetails("v1")!!.visit.id)
    }

    @Test fun replaceLinesAndCascadeDelete() = runBlocking {
        db.catalogDao().insertAll(listOf(custody("c1"), custody("c2")))
        dao.save(visit("v1"), listOf(VisitItemEntity("v1", "c1", 2)))
        dao.replaceLines("v1", listOf(VisitItemEntity("v1", "c2", 5)))
        assertEquals(listOf("c2"), dao.getWithDetails("v1")!!.lines.map { it.itemId })
        dao.deleteById("v1")
        assertTrue(dao.getWithDetails("v1") == null)
    }

    @Test fun replacingACatalogRowKeepsVisitLines() = runBlocking {
        db.catalogDao().upsert(custody("c1", "Pen"))
        dao.save(visit("v1"), listOf(VisitItemEntity("v1", "c1", 2)))
        db.catalogDao().upsert(custody("c1", "Cap"))
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
        dao.save(visit("p", type = VisitType.Planning, status = VisitStatus.PendingApproval, requestedAtMillis = 5, backendId = "b1"), emptyList())
        assertEquals(1, dao.countPending().first())
        assertEquals(listOf("p"), dao.pendingPaged(null, null).allRows().map { it.visit.id })
        assertTrue(dao.startablePaged().allRows().isEmpty())
        assertEquals(1, dao.applyDecision("b1", true, 9))
        assertEquals(0, dao.countPending().first())
        assertTrue(dao.pendingPaged(null, null).allRows().isEmpty())
        val started = dao.startablePaged().allRows().single().visit
        assertEquals(VisitType.Planned, started.type)
        assertEquals(VisitStatus.Scheduled, started.status)
        assertEquals(9L, started.decidedAtMillis)
    }

    @Test fun rejectedRequestMovesToTheRejectedListUntilDismissed() = runBlocking {
        dao.save(visit("p", type = VisitType.Planning, status = VisitStatus.PendingApproval, backendId = "b1"), emptyList())
        dao.applyDecision("b1", false, 9)
        assertTrue(dao.pendingPaged(null, null).allRows().isEmpty())
        val row = dao.rejectedPaged(null, null).allRows().single().visit
        assertEquals(VisitType.Planning, row.type)
        assertEquals(VisitStatus.Rejected, row.status)
        assertEquals(1, dao.rejectedFilteredCount(null, null).first())
        assertTrue(dao.startablePaged().allRows().isEmpty())
        assertEquals(1, dao.dismissRejected("p"))
        assertNull(dao.getById("p"))
    }

    @Test fun unknownBackendIdChangesNothingAndDismissIgnoresPending() = runBlocking {
        dao.save(visit("p", type = VisitType.Planning, status = VisitStatus.PendingApproval, backendId = "b1"), emptyList())
        assertEquals(0, dao.applyDecision("nope", true, 9))
        assertEquals(0, dao.dismissRejected("p"))
        assertNotNull(dao.getById("p"))
    }

    @Test fun cancelOnlyWorksOnRequestsNotUploadedYet() = runBlocking {
        dao.save(visit("local", type = VisitType.Planning, status = VisitStatus.PendingApproval, syncStatus = SyncStatus.NotSynced), emptyList())
        dao.save(visit("sent", type = VisitType.Planning, status = VisitStatus.PendingApproval, syncStatus = SyncStatus.Synced), emptyList())
        assertEquals(1, dao.cancelRequest("local"))
        assertEquals(0, dao.cancelRequest("sent"))
    }

    @Test fun requiredAndDoneListsUseTheCompletedTime() = runBlocking {
        db.accountDao().save(account("a1", "A"))
        db.accountDao().save(account("a2", "B"))
        db.addDoctor("a1", classCategory = "A")
        db.addDoctor("a2", classCategory = "B")
        dao.save(visit("todo-am", accountId = "a1", type = VisitType.Planned, dateTimeMillis = 100), emptyList())
        dao.save(visit("todo-pm", accountId = "a2", type = VisitType.Planned, shift = Shift.Pm, dateTimeMillis = 200), emptyList())
        dao.save(visit("done-planned", accountId = "a1", type = VisitType.Planned, status = VisitStatus.Completed, completedAtMillis = 300), emptyList())
        dao.save(visit("done-double", type = VisitType.Unplanned, double = true, syncStatus = SyncStatus.NotSynced, status = VisitStatus.Completed, completedAtMillis = 400), emptyList())
        assertEquals(listOf("todo-am", "todo-pm"), dao.requiredPaged(null, null, null, null, null, null, null, "", null, null, 1.0).allRows().map { it.visit.id })
        assertEquals(listOf("todo-pm"), dao.requiredPaged(Shift.Pm, null, null, null, null, null, null, "", null, null, 1.0).allRows().map { it.visit.id })
        assertEquals(listOf("todo-am"), dao.requiredPaged(null, "A", null, null, null, null, null, "", null, null, 1.0).allRows().map { it.visit.id })
        assertEquals(2, dao.requiredCount(null, null, null, null, null, null, null, "").first())
        assertEquals(listOf("done-double", "done-planned"), dao.donePaged(null, null, null, null, null, null, null, null, null, "").allRows().map { it.visit.id })
        assertEquals(listOf("done-double"), dao.donePaged(null, VisitMode.Double, null, null, null, null, null, null, null, "").allRows().map { it.visit.id })
        assertEquals(listOf("done-double"), dao.donePaged(null, null, SyncStatus.NotSynced, null, null, null, null, null, null, "").allRows().map { it.visit.id })
        assertEquals(1, dao.doneCount(Shift.Am, VisitMode.Double, null, null, null, null, null, null, null, "").first())
        assertEquals(listOf("todo-am", "todo-pm"), dao.startablePaged().allRows().map { it.visit.id }.sorted())
    }

    @Test fun requiredAndDoneListsFilterByCityDistrictSpecialtyAndPeriod() = runBlocking {
        db.geoDao().upsertCities(listOf(CityEntity("c1", "Cairo"), CityEntity("c2", "Giza")))
        db.geoDao().upsertDistricts(listOf(DistrictEntity("d1", "c1", "Nasr City"), DistrictEntity("d2", "c2", "Dokki")))
        db.accountDao().save(account("a1", districtId = "d1"))
        db.accountDao().save(account("a2", districtId = "d2"))
        db.addDoctor("a1", specialty = "Cardiology")
        db.addDoctor("a2", specialty = "Dermatology")
        dao.save(visit("cairo", accountId = "a1", type = VisitType.Planned, dateTimeMillis = 1_000), emptyList())
        dao.save(visit("giza", accountId = "a2", type = VisitType.Planned, dateTimeMillis = 2_000), emptyList())
        dao.save(visit("done-cairo", accountId = "a1", status = VisitStatus.Completed, completedAtMillis = 1_500), emptyList())
        dao.save(visit("done-giza", accountId = "a2", status = VisitStatus.Completed, completedAtMillis = 2_500), emptyList())
        fun required(city: String? = null, district: String? = null, specialty: String? = null, from: Long? = null, to: Long? = null) =
            dao.requiredPaged(null, null, city, district, specialty, from, to, "", null, null, 1.0)
        assertEquals(listOf("cairo"), required(city = "c1").allRows().map { it.visit.id })
        assertEquals(listOf("giza"), required(district = "d2").allRows().map { it.visit.id })
        assertEquals(listOf("cairo"), required(specialty = "Cardiology").allRows().map { it.visit.id })
        assertEquals(emptyList<String>(), required(city = "c1", district = "d2").allRows().map { it.visit.id })
        assertEquals(listOf("giza"), required(from = 1_500, to = 3_000).allRows().map { it.visit.id })
        assertEquals(listOf("cairo"), required(to = 2_000).allRows().map { it.visit.id })
        assertEquals(1, dao.requiredCount(null, null, "c2", null, null, null, null, "").first())
        assertEquals(
            listOf("done-giza"),
            dao.donePaged(null, null, null, null, "c2", null, null, null, null, "").allRows().map { it.visit.id },
        )
        assertEquals(
            listOf("done-cairo"),
            dao.donePaged(null, null, null, null, null, null, "Cardiology", 1_000, 2_000, "").allRows().map { it.visit.id },
        )
        assertEquals(1, dao.doneCount(null, null, null, null, null, null, null, 2_000, 3_000, "").first())
    }

    @Test fun aRefusedUploadIsMarkedFailedStaysToUploadAndCanBeFilteredByIt() = runBlocking {
        dao.save(visit("wait", status = VisitStatus.Completed, completedAtMillis = 1, syncStatus = SyncStatus.NotSynced), emptyList())
        dao.save(visit("fail", status = VisitStatus.Completed, completedAtMillis = 2, syncStatus = SyncStatus.NotSynced), emptyList())
        dao.save(visit("done", status = VisitStatus.Completed, completedAtMillis = 3, syncStatus = SyncStatus.Synced), emptyList())
        assertEquals(1, dao.markFailed("fail"))
        assertEquals(0, dao.markFailed("done"))
        assertEquals(SyncStatus.Failed, dao.getById("fail")!!.syncStatus)
        assertEquals(SyncStatus.Synced, dao.getById("done")!!.syncStatus)
        assertEquals(setOf("wait", "fail"), dao.getNotSynced().map { it.id }.toSet())
        fun done(sync: SyncStatus?) = dao.donePaged(null, null, sync, null, null, null, null, null, null, "")
        assertEquals(listOf("fail"), done(SyncStatus.Failed).allRows().map { it.visit.id })
        assertEquals(listOf("wait"), done(SyncStatus.NotSynced).allRows().map { it.visit.id })
        assertEquals(listOf("done"), done(SyncStatus.Synced).allRows().map { it.visit.id })
        dao.markSynced("fail", "b1")
        assertEquals(SyncStatus.Synced, dao.getById("fail")!!.syncStatus)
    }

    @Test fun scheduledFromGivesTodayFirstThenNearestOfEachDayWithTheLimitAppliedAfterOrdering() = runBlocking {
        val day = 86_400_000L * 40
        suspend fun place(id: String, km: Int) = db.accountDao().save(account(id).copy(latitude = 30.0 + km * 0.009, longitude = 31.0))
        place("near", 1)
        place("far", 20)
        dao.save(visit("today-far", accountId = "far", type = VisitType.Planned, dateTimeMillis = day + 1_000), emptyList())
        dao.save(visit("today-near-late", accountId = "near", type = VisitType.Planned, dateTimeMillis = day + 9_000_000), emptyList())
        dao.save(visit("tomorrow-near", accountId = "near", type = VisitType.Planned, dateTimeMillis = day + 86_400_000 + 1_000), emptyList())
        dao.save(visit("yesterday", accountId = "near", type = VisitType.Planned, dateTimeMillis = day - 1_000), emptyList())
        suspend fun next(limit: Int) = dao.scheduledFrom(day, 30.0, 31.0, 1.0, limit).first().map { it.visit.id }
        assertEquals(listOf("today-near-late", "today-far", "tomorrow-near"), next(10))
        assertEquals(listOf("today-near-late"), next(1))
    }

    @Test fun requiredVisitsComeNearestToTheRepFirstAcrossDaysAndUnknownPlacesLast() = runBlocking {
        suspend fun place(id: String, km: Int?) = db.accountDao().save(
            account(id).copy(latitude = if (km == null) 0.0 else 30.0 + km * 0.009, longitude = if (km == null) 0.0 else 31.0),
        )
        place("near", 1)
        place("middle", 5)
        place("far", 20)
        place("unknown", null)
        dao.save(visit("far-early", accountId = "far", type = VisitType.Planned, dateTimeMillis = 1_000), emptyList())
        dao.save(visit("near-next-day", accountId = "near", type = VisitType.Planned, dateTimeMillis = 100_000_000), emptyList())
        dao.save(visit("unknown-early", accountId = "unknown", type = VisitType.Planned, dateTimeMillis = 500), emptyList())
        dao.save(visit("middle-late", accountId = "middle", type = VisitType.Planned, dateTimeMillis = 9_000), emptyList())
        dao.save(visit("no-account", type = VisitType.Planned, dateTimeMillis = 100), emptyList())
        fun required(lat: Double?) = dao.requiredPaged(null, null, null, null, null, null, null, "", lat, 31.0, 1.0)
        assertEquals(
            listOf("near-next-day", "middle-late", "far-early", "no-account", "unknown-early"),
            required(30.0).allRows().map { it.visit.id },
        )
        assertEquals(
            listOf("no-account", "unknown-early", "far-early", "middle-late", "near-next-day"),
            required(null).allRows().map { it.visit.id },
        )
    }

    @Test fun searchMatchesTheClinicTheDoctorNameAndTheSpecialtyAndKeepsTheOtherFilters() = runBlocking {
        db.accountDao().save(account("a1", name = "Nile Clinic"))
        db.accountDao().save(account("a2", name = "Delta Hospital"))
        db.addDoctor("a1", specialty = "Cardiology", id = "mona")
        db.addDoctor("a2", specialty = "Dermatology", id = "tarek")
        dao.save(visit("nile", accountId = "a1", status = VisitStatus.Completed, completedAtMillis = 1_000, syncStatus = SyncStatus.Synced), emptyList())
        dao.save(visit("delta", accountId = "a2", status = VisitStatus.Completed, completedAtMillis = 2_000, syncStatus = SyncStatus.NotSynced), emptyList())
        suspend fun done(query: String, sync: SyncStatus? = null) =
            dao.donePaged(null, null, sync, null, null, null, null, null, null, query).allRows().map { it.visit.id }
        assertEquals(listOf("delta", "nile"), done(""))
        assertEquals(listOf("nile"), done("nile"))
        assertEquals(listOf("delta"), done("Hospital"))
        assertEquals(listOf("nile"), done("mon"))
        assertEquals(listOf("delta"), done("Dermato"))
        assertEquals(emptyList<String>(), done("nobody"))
        assertEquals(emptyList<String>(), done("Hospital", sync = SyncStatus.Synced))
        assertEquals(listOf("delta"), done("Hospital", sync = SyncStatus.NotSynced))
        assertEquals(1, dao.doneCount(null, null, null, null, null, null, null, null, null, "Dermato").first())
        assertEquals(0, dao.requiredCount(null, null, null, null, null, null, null, "Dermato").first())
    }

    @Test fun cancelVisitOnlyChangesAToDoVisitAndKeepsTheReason() = runBlocking {
        dao.save(visit("todo", type = VisitType.Planned, dateTimeMillis = 100), emptyList())
        dao.save(visit("done", type = VisitType.Planned, status = VisitStatus.Completed, completedAtMillis = 5), emptyList())
        dao.save(visit("request", type = VisitType.Planning, status = VisitStatus.PendingApproval), emptyList())
        assertEquals(1, dao.cancelVisit("todo", "travelling"))
        val cancelled = dao.getById("todo")!!
        assertEquals(VisitStatus.Missed, cancelled.status)
        assertEquals("travelling", cancelled.cancelReason)
        assertEquals(SyncStatus.NotSynced, cancelled.syncStatus)
        assertEquals(0, dao.cancelVisit("todo", "again"))
        assertEquals(0, dao.cancelVisit("done", "late"))
        assertEquals(0, dao.cancelVisit("request", "late"))
        assertEquals(0, dao.cancelVisit("nope", "late"))
        assertEquals(VisitStatus.Completed, dao.getById("done")!!.status)
        assertEquals(emptyList<String>(), dao.requiredPaged(null, null, null, null, null, null, null, "", null, null, 1.0).allRows().map { it.visit.id })
        assertEquals(listOf("todo"), dao.missedPaged(null, null).allRows().map { it.visit.id })
    }

    @Test fun scheduledFromGivesToDoVisitsFromThatTimeOnSoonestFirst() = runBlocking {
        dao.save(visit("late", dateTimeMillis = 1_900), emptyList())
        dao.save(visit("early", dateTimeMillis = 1_100), emptyList())
        dao.save(visit("middle", dateTimeMillis = 1_500), emptyList())
        dao.save(visit("next-day", dateTimeMillis = 5_000), emptyList())
        dao.save(visit("before", dateTimeMillis = 999), emptyList())
        dao.save(visit("done", status = VisitStatus.Completed, dateTimeMillis = 1_200, completedAtMillis = 1_300), emptyList())
        dao.save(visit("missed", status = VisitStatus.Missed, dateTimeMillis = 1_200), emptyList())
        assertEquals(listOf("early", "middle", "late", "next-day"), dao.scheduledFrom(1_000, null, null, 1.0, 10).first().map { it.visit.id })
    }

    @Test fun markMissedOnlyMovesScheduledVisitsOfEarlierDays() = runBlocking {
        dao.save(visit("old", type = VisitType.Planned, dateTimeMillis = 1_000), emptyList())
        dao.save(visit("today", type = VisitType.Planned, dateTimeMillis = 5_000), emptyList())
        dao.save(visit("undated", type = VisitType.Planned, dateTimeMillis = 0), emptyList())
        dao.save(visit("done", type = VisitType.Planned, status = VisitStatus.Completed, dateTimeMillis = 1_000, completedAtMillis = 1_500), emptyList())
        dao.save(visit("request", type = VisitType.Planning, status = VisitStatus.PendingApproval, dateTimeMillis = 1_000), emptyList())
        assertEquals(1, dao.markMissed(5_000))
        assertEquals(VisitStatus.Missed, dao.getById("old")!!.status)
        assertEquals(VisitStatus.Scheduled, dao.getById("today")!!.status)
        assertEquals(VisitStatus.Scheduled, dao.getById("undated")!!.status)
        assertEquals(VisitStatus.Completed, dao.getById("done")!!.status)
        assertEquals(VisitStatus.PendingApproval, dao.getById("request")!!.status)
        assertEquals(listOf("old"), dao.missedPaged(null, null).allRows().map { it.visit.id })
        assertEquals(1, dao.missedCount(null, null).first())
        assertEquals(listOf("today", "undated"), dao.requiredPaged(null, null, null, null, null, null, null, "", null, null, 1.0).allRows().map { it.visit.id }.sorted())
        assertEquals(0, dao.markMissed(5_000))
    }

    @Test fun pendingRequestsAreFilteredByShiftAndClass() = runBlocking {
        db.accountDao().save(account("a1", "A"))
        db.accountDao().save(account("a2", "B"))
        db.addDoctor("a1", classCategory = "A")
        db.addDoctor("a2", classCategory = "B")
        dao.save(visit("am", accountId = "a1", type = VisitType.Planning, status = VisitStatus.PendingApproval, requestedAtMillis = 1), emptyList())
        dao.save(visit("pm", accountId = "a2", type = VisitType.Planning, status = VisitStatus.PendingApproval, requestedAtMillis = 2, shift = Shift.Pm), emptyList())
        assertEquals(2, dao.pendingFilteredCount(null, null).first())
        assertEquals(listOf("pm"), dao.pendingPaged(Shift.Pm, null).allRows().map { it.visit.id })
        assertEquals(listOf("am"), dao.pendingPaged(null, "A").allRows().map { it.visit.id })
        assertEquals(0, dao.pendingFilteredCount(Shift.Pm, "A").first())
        dao.save(visit("done-b", accountId = "a2", status = VisitStatus.Completed, completedAtMillis = 9), emptyList())
        assertEquals(listOf("done-b"), dao.donePaged(null, null, null, "B", null, null, null, null, null, "").allRows().map { it.visit.id })
    }

    @Test fun doneBetweenIsTheRepsFinishedVisitsOfTheDayInTimeOrder() = runBlocking {
        dao.save(visit("late", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = 1_800, completedAtMillis = 1_900), emptyList())
        dao.save(visit("early", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = 1_100, completedAtMillis = 1_200), emptyList())
        dao.save(visit("other-rep", repId = "r2", status = VisitStatus.Completed, dateTimeMillis = 1_500, completedAtMillis = 1_600), emptyList())
        dao.save(visit("todo", repId = "r1", status = VisitStatus.Scheduled, dateTimeMillis = 1_500), emptyList())
        dao.save(visit("next-day", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = 2_000, completedAtMillis = 2_100), emptyList())
        dao.save(visit("day-before", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = 999, completedAtMillis = 999), emptyList())
        assertEquals(listOf("early", "late"), dao.doneBetween("r1", 1_000, 2_000).first().map { it.visit.id })
        assertTrue(dao.doneBetween("nobody", 1_000, 2_000).first().isEmpty())
    }

    @Test fun clearEmptiesTable() = runBlocking {
        dao.save(visit("v1"), emptyList())
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }
}
