package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.SyncStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepo {
    fun orders(query: String = ""): Flow<PagingData<Order>>
    fun ordersFiltered(accountId: String?, repId: String?, syncStatus: SyncStatus?, fromMillis: Long, toMillis: Long): Flow<PagingData<Order>>
    suspend fun get(id: String): Order?

    /** False when the order is already uploaded: it can no longer be changed. */
    suspend fun save(order: Order): Boolean

    /** False when the order is already uploaded. */
    suspend fun cancel(id: String): Boolean

    suspend fun notSynced(): List<Order>
    suspend fun markSynced(id: String, backendId: String?)
}
