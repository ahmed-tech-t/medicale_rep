package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A visit stands in two states: the admin's answer and the rep's work.
 * Given each screen status, then it splits into the same pair and joins back to itself.
 * Given a pending or rejected request, then the answer wins over the work.
 */
class VisitStatesTest {

    @Test
    fun `every status splits and joins back to itself`() {
        VisitStatus.entries.forEach { assertEquals(it, visitStatusOf(it.approval, it.execution)) }
    }

    @Test
    fun `statuses split into the expected pair`() {
        assertEquals(ApprovalStatus.Pending to ExecutionStatus.Scheduled, VisitStatus.PendingApproval.approval to VisitStatus.PendingApproval.execution)
        assertEquals(ApprovalStatus.Approved to ExecutionStatus.Scheduled, VisitStatus.Scheduled.approval to VisitStatus.Scheduled.execution)
        assertEquals(ApprovalStatus.Approved to ExecutionStatus.Completed, VisitStatus.Completed.approval to VisitStatus.Completed.execution)
        assertEquals(ApprovalStatus.Approved to ExecutionStatus.Cancelled, VisitStatus.Missed.approval to VisitStatus.Missed.execution)
        assertEquals(ApprovalStatus.Rejected, VisitStatus.Rejected.approval)
    }

    @Test
    fun `the answer wins over the work`() {
        assertEquals(VisitStatus.PendingApproval, visitStatusOf(ApprovalStatus.Pending, ExecutionStatus.Completed))
        assertEquals(VisitStatus.Rejected, visitStatusOf(ApprovalStatus.Rejected, ExecutionStatus.Cancelled))
    }
}
