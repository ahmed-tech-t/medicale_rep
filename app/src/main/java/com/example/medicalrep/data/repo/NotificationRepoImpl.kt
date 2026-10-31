package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.NotificationDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Notification
import com.example.medicalrep.domain.repo.NotificationRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(private val dao: NotificationDao) : NotificationRepo {

    override fun notificationsOf(repId: String): Flow<List<Notification>> = dao.ofRep(repId).map { list -> list.map { it.toDomain() } }

    override fun unreadCount(repId: String): Flow<Int> = dao.unreadCount(repId)

    override suspend fun save(notification: Notification) {
        dao.upsert(notification.toEntity())
    }

    override suspend fun markRead(id: String) {
        dao.markRead(id)
    }
}
