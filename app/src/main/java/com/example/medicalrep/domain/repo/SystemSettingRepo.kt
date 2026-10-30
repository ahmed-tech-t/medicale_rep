package com.example.medicalrep.domain.repo

import kotlinx.coroutines.flow.Flow

interface SystemSettingRepo {
    /** The value of [key]; null when the server never sent it. */
    fun get(key: String): Flow<String?>
    suspend fun set(key: String, value: String)
}
