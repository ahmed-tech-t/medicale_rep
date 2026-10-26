package com.example.medicalrep.domain.models

/** When one Data Center card (for example "products") was last updated. */
data class SyncInfo(
    val key: String,
    val lastUpdatedMillis: Long,
)
