package com.example.medicalrep.domain.models

enum class RequestCategory { OfficeWork, Leave, VisitPlan }

enum class RequestStatus { Pending, Approved, Rejected }

/** A request the rep sends the admin: office work, leave, or a visit plan, for one day and shift. */
data class RepRequest(
    val id: String,
    val repId: String,
    val dateMillis: Long,
    val category: RequestCategory,
    val subType: String = "",
    val shift: Shift? = null,
    val status: RequestStatus = RequestStatus.Pending,
    val notes: String = "",
    val createdAtMillis: Long = 0L,
    val decidedAtMillis: Long? = null,
)
