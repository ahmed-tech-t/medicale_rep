package com.example.medicalrep.domain.models

/** Where a visit plan request stands: the admin decides on the server. */
enum class ApprovalStatus {
    Pending,
    Approved,
    Rejected,
}
