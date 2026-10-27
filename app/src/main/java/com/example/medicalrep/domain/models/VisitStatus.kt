package com.example.medicalrep.domain.models

/** Where a visit stands. [Planned][VisitType.Planned] visits start Scheduled; plan requests wait for the admin. */
enum class VisitStatus {
    /** A plan request the admin has not answered. */
    PendingApproval,

    /** To do: planned, or a request the admin approved. */
    Scheduled,

    /** The rep did the visit and ended it. */
    Completed,

    /** Scheduled, but its day passed without the visit. */
    Missed,

    /** A plan request the admin refused. */
    Rejected,
}
