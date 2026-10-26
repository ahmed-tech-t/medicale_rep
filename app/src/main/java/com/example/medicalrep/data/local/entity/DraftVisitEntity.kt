package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** The single pending visit: the form fields are kept as one JSON text, so the visits table stays clean. */
@Entity(tableName = "visit_draft")
data class DraftVisitEntity(
    @PrimaryKey val id: Int = DRAFT_ID,
    val step: Int,
    val json: String,
) {
    companion object {
        const val DRAFT_ID = 1
    }
}
