package com.example.medicalrep.domain.models

/** Whether the admin accepted the visit: a plan request starts Pending; every other visit is Approved. */
enum class ApprovalStatus { Pending, Approved, Rejected }

/** Whether the rep did the visit: Cancelled is a visit that was not done (its day passed, or the rep dropped it). */
enum class ExecutionStatus { Scheduled, Completed, Cancelled }

/** The two states the screens call a [VisitStatus]. */
val VisitStatus.approval: ApprovalStatus
    get() = when (this) {
        VisitStatus.PendingApproval -> ApprovalStatus.Pending
        VisitStatus.Rejected -> ApprovalStatus.Rejected
        else -> ApprovalStatus.Approved
    }

val VisitStatus.execution: ExecutionStatus
    get() = when (this) {
        VisitStatus.Completed -> ExecutionStatus.Completed
        VisitStatus.Missed -> ExecutionStatus.Cancelled
        else -> ExecutionStatus.Scheduled
    }

/** Back to the screens' state: the approval decides first, then the execution. */
fun visitStatusOf(approval: ApprovalStatus, execution: ExecutionStatus): VisitStatus = when {
    approval == ApprovalStatus.Pending -> VisitStatus.PendingApproval
    approval == ApprovalStatus.Rejected -> VisitStatus.Rejected
    execution == ExecutionStatus.Completed -> VisitStatus.Completed
    execution == ExecutionStatus.Cancelled -> VisitStatus.Missed
    else -> VisitStatus.Scheduled
}
