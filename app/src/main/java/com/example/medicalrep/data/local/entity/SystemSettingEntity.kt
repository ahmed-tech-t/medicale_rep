package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_settings")
data class SystemSettingEntity(@PrimaryKey val key: String, val value: String)
