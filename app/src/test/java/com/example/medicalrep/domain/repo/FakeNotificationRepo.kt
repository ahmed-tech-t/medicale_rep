package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNotificationRepo(notifications: List<Notification> = emptyList()) : NotificationRepo {
    val current = MutableStateFlow(notifications)

    override fun notificationsOf(repId: String): Flow<List<Notification>> =
        current.map { list -> list.filter { it.repId == repId }.sortedByDescending { it.createdAtMillis } }

    override fun unreadCount(repId: String): Flow<Int> =
        current.map { list -> list.count { it.repId == repId && !it.isRead } }

    override suspend fun save(notification: Notification) {
        current.value = current.value.filter { it.id != notification.id } + notification
    }

    override suspend fun markRead(id: String) {
        current.value = current.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }
}
