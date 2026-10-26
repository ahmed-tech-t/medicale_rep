package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.DraftVisit
import kotlinx.coroutines.flow.Flow

/** At most one pending (saved, not finished) visit exists. */
interface DraftVisitRepo {
    fun draft(): Flow<DraftVisit?>

    /** Replaces any older draft; a [Visit.voiceNote] is written to a file. */
    suspend fun save(draft: DraftVisit)

    /** Deletes the draft and its voice note file. */
    suspend fun discard()
}
