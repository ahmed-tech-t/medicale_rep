package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepo {
    /** The rep's notifications, newest first. */
    fun notificationsOf(repId: String): Flow<List<Notification>>
    fun count(repId: String): Flow<Int>
    suspend fun save(notification: Notification)
}
