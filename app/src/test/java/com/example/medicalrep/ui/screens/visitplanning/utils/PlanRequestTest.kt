package com.example.medicalrep.ui.screens.visitplanning.utils

import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** A plan request copies who and where from the visit it was made from, and starts pending and not uploaded. */
class PlanRequestTest {

    @Test
    fun `request keeps account rep and shift and starts pending`() {
        val source = Visit(id = "v1", accountId = "a1", repId = "r1", supervisorId = "s1", shift = Shift.Pm, visitMode = VisitMode.Double, completedAtMillis = 5L)
        val request = source.asPlanRequest(newId = "req-1", dateMillis = 1_000L, nowMillis = 2_000L)
        assertEquals("req-1", request.id)
        assertEquals("a1", request.accountId)
        assertEquals("r1", request.repId)
        assertEquals(Shift.Pm, request.shift)
        assertEquals(VisitType.Planning, request.type)
        assertEquals(VisitStatus.PendingApproval, request.status)
        assertEquals(SyncStatus.NotSynced, request.syncStatus)
        assertEquals(1_000L, request.dateTimeMillis)
        assertEquals(2_000L, request.requestedAtMillis)
    }

    @Test
    fun `request is a fresh single visit without supervisor, result or completion`() {
        val source = Visit(id = "v1", accountId = "a1", supervisorId = "s1", visitMode = VisitMode.Double, completedAtMillis = 5L, backendId = "b1", noIssueFound = false)
        val request = source.asPlanRequest("req-1", 1L, 2L)
        assertEquals(VisitMode.Single, request.visitMode)
        assertNull(request.supervisorId)
        assertNull(request.completedAtMillis)
        assertNull(request.backendId)
        assertEquals(true, request.noIssueFound)
    }
}
