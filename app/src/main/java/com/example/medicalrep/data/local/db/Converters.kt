package com.example.medicalrep.data.local.db

import androidx.room.TypeConverter
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType

/** Enums are stored by Room as their names; the sealed types are stored as the same kind of text. */
class Converters {

    @TypeConverter
    fun fromVisitType(type: VisitType): String = when (type) {
        VisitType.Planning -> "Planning"
        VisitType.Planned -> "Planned"
        VisitType.Unplanned -> "Unplanned"
    }

    @TypeConverter
    fun toVisitType(value: String): VisitType = when (value) {
        "Planning" -> VisitType.Planning
        "Planned" -> VisitType.Planned
        else -> VisitType.Unplanned
    }

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = when (status) {
        SyncStatus.Synced -> "Synced"
        SyncStatus.NotSynced -> "NotSynced"
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus =
        if (value == "Synced") SyncStatus.Synced else SyncStatus.NotSynced

    @TypeConverter
    fun fromTexts(values: List<String>): String = values.joinToString("\n")

    @TypeConverter
    fun toTexts(value: String): List<String> = value.lines().filter { it.isNotEmpty() }

    @TypeConverter
    fun fromVisitMode(mode: VisitMode): String = when (mode) {
        VisitMode.Single -> "Single"
        VisitMode.Double -> "Double"
    }

    @TypeConverter
    fun toVisitMode(value: String): VisitMode = if (value == "Double") VisitMode.Double else VisitMode.Single
}
