package com.example.medicalrep.data.seed

import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.ApprovalStatus
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.UserRole
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The debug seed must be usable by every wired screen:
 * 1. Ids are unique in every list.
 * 2. Every reference exists: account -> supervisor, visit -> account / rep / supervisor, visit line -> custody.
 * 3. All four account types, three classes, several cities, districts and specialties are present.
 * 4. Products exist; custody has gifts and samples with stock left.
 * 5. Visits cover: planned (both shifts), done synced and not synced, double visits with a supervisor,
 *    pending and rejected plan requests (with a reason), and dates relative to "now".
 */
class SeedDataTest {

    private val now = 1_792_000_000_000L
    private val data = SeedDataFactory.build(now)

    @Test
    fun `ids are unique`() {
        assertEquals(data.users.size, data.users.map { it.id }.toSet().size)
        assertEquals(data.accounts.size, data.accounts.map { it.id }.toSet().size)
        assertEquals(data.products.size, data.products.map { it.id }.toSet().size)
        assertEquals(data.custody.size, data.custody.map { it.id }.toSet().size)
        assertEquals(data.visits.size, data.visits.map { it.id }.toSet().size)
    }

    @Test
    fun `every reference points at an existing row`() {
        val supervisors = data.users.filter { it.role == UserRole.Supervisor }.map { it.id }.toSet()
        val reps = data.users.filter { it.role == UserRole.Rep }
        assertTrue(supervisors.isNotEmpty() && reps.isNotEmpty())
        assertTrue(reps.all { it.supervisorId in supervisors })
        assertTrue(data.accounts.all { it.supervisorId in supervisors })
        val accountIds = data.accounts.map { it.id }.toSet()
        val repIds = reps.map { it.id }.toSet()
        val custodyIds = data.custody.map { it.id }.toSet()
        data.visits.forEach { visit ->
            assertTrue(visit.accountId in accountIds)
            assertTrue(visit.repId in repIds)
            assertTrue(visit.supervisorId == null || visit.supervisorId in supervisors)
            (visit.gifts.orEmpty() + visit.products.orEmpty()).forEach { assertTrue(it.id in custodyIds) }
        }
    }

    @Test
    fun `accounts cover every type, class and several places`() {
        val types = data.accounts.mapNotNull { AccountType.fromEntityType(it.entityType) }.toSet()
        assertEquals(AccountType.entries.toSet(), types)
        assertEquals(setOf("A", "B", "C"), data.accounts.map { it.classCategory }.toSet())
        assertTrue(data.accounts.map { it.city }.toSet().size >= 2)
        assertTrue(data.accounts.map { it.district }.toSet().size >= 2)
        assertTrue(data.accounts.map { it.specialty }.toSet().size >= 3)
        assertTrue(data.accounts.all { it.syncStatus == SyncStatus.Synced })
    }

    @Test
    fun `products and custody have stock`() {
        assertTrue(data.products.size >= 5 && data.products.all { it.price > 0 })
        assertEquals(setOf(ProductType.Gift, ProductType.Product), data.custody.map { it.kind }.toSet())
        assertTrue(data.custody.all { it.remaining > 0 })
    }

    @Test
    fun `visits cover the filters of every screen`() {
        val planned = data.visits.filter { it.type == VisitType.Planned }
        assertTrue(planned.size >= 4)
        assertEquals(setOf(Shift.Am, Shift.Pm), planned.map { it.shift }.toSet())
        val done = data.visits.filter { it.type == VisitType.Unplanned }
        assertEquals(setOf(SyncStatus.Synced, SyncStatus.NotSynced), done.map { it.syncStatus }.toSet())
        assertEquals(setOf(Shift.Am, Shift.Pm), done.map { it.shift }.toSet())
        val double = data.visits.filter { it.visitMode == VisitMode.Double }
        assertTrue(double.isNotEmpty() && double.all { it.supervisorId != null })
        val requests = data.visits.filter { it.type == VisitType.Planning }
        assertEquals(setOf(ApprovalStatus.Pending, ApprovalStatus.Rejected), requests.map { it.approvalStatus }.toSet())
        assertTrue(requests.filter { it.approvalStatus == ApprovalStatus.Rejected }.all { !it.decisionNote.isNullOrBlank() })
        assertTrue(requests.all { it.requestedAtMillis != null })
    }

    @Test
    fun `done visits are in the past and planned ones are today or later`() {
        data.visits.filter { it.type == VisitType.Unplanned }.forEach { assertTrue(it.dateTimeMillis!! < now) }
        data.visits.filter { it.type == VisitType.Planned }.forEach { assertTrue(it.dateTimeMillis!! >= now - 12 * HOUR) }
    }

    private companion object {
        const val HOUR = 3_600_000L
    }
}
