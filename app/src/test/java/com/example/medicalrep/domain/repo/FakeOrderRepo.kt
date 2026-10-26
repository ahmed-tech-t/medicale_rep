package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory orders for ViewModel tests; an uploaded order cannot be changed or cancelled, like the real one. */
class FakeOrderRepo(initial: List<Order> = emptyList()) : OrderRepo {
    val current = MutableStateFlow(initial)

    override fun orders(query: String): Flow<PagingData<Order>> = current.map { PagingData.from(it) }

    override fun ordersFiltered(accountId: String?, repId: String?, syncStatus: SyncStatus?, fromMillis: Long, toMillis: Long): Flow<PagingData<Order>> =
        current.map { PagingData.from(it) }

    override suspend fun get(id: String): Order? = current.value.firstOrNull { it.id == id }

    override suspend fun save(order: Order): Boolean {
        if (get(order.id)?.syncStatus == SyncStatus.Synced) return false
        current.value = current.value.filter { it.id != order.id } + order
        return true
    }

    override suspend fun cancel(id: String): Boolean {
        val order = get(id) ?: return false
        if (order.syncStatus == SyncStatus.Synced) return false
        current.value = current.value.filter { it.id != id }
        return true
    }

    override suspend fun notSynced(): List<Order> = current.value.filter { it.syncStatus == SyncStatus.NotSynced }
    override suspend fun markSynced(id: String, backendId: String?) = Unit
}
