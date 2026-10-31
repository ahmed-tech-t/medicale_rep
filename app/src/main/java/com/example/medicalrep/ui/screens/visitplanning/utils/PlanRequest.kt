package com.example.medicalrep.ui.screens.visitplanning.utils

import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType

/** A request to visit the same account again on [dateMillis]; it waits for the admin and is uploaded later. */
fun Visit.asPlanRequest(newId: String, dateMillis: Long, nowMillis: Long) = Visit(
    id = newId,
    accountId = accountId,
    repId = repId,
    shift = shift,
    type = VisitType.Planning,
    visitMode = VisitMode.Single,
    dateTimeMillis = dateMillis,
    syncStatus = SyncStatus.NotSynced,
    status = VisitStatus.PendingApproval,
    requestedAtMillis = nowMillis,
    doctors = doctors,
)
