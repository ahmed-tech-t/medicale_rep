package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.OrderDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.toItems
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.repo.OrderRepo
import javax.inject.Inject

class OrderRepoImpl @Inject constructor(private val dao: OrderDao) : OrderRepo {

    override fun orders(query: String) = pagedFlow({ dao.search(normalizeSearch(query)) }) { it.toDomain() }

    override fun ordersFiltered(accountId: String?, repId: String?, syncStatus: SyncStatus?, fromMillis: Long, toMillis: Long) =
        pagedFlow({ dao.pagedFiltered(accountId, repId, syncStatus, fromMillis, toMillis) }) { it.toDomain() }

    override suspend fun get(id: String): Order? = dao.getWithDetails(id)?.toDomain()

    override suspend fun save(order: Order) = dao.save(order.toEntity(), order.toItems())

    override suspend fun cancel(id: String) = dao.cancel(id) > 0

    override suspend fun notSynced(): List<Order> = dao.getNotSynced().mapNotNull { dao.getWithDetails(it.id)?.toDomain() }

    override suspend fun markSynced(id: String, backendId: String?) {
        dao.markSynced(id, backendId)
    }
}
