package com.example.medicalrep.domain.repo

/** What a fake repo was asked for: one rep's data between two times. */
data class RepDayRequest(val repId: String, val fromMillis: Long, val toMillis: Long)
