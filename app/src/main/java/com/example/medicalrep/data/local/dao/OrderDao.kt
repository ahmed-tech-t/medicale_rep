package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.relation.OrderWithDetails
import com.example.medicalrep.domain.models.SyncStatus
import kotlinx.coroutines.flow.Flow

/** Orders are only uploaded: the rep can change or cancel one until it is uploaded, never after. */
@Dao
abstract class OrderDao : BaseDao<OrderEntity> {

    @Query("SELECT * FROM orders WHERE id = :id")
    abstract suspend fun getById(id: String): OrderEntity?

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id")
    abstract suspend fun getWithDetails(id: String): OrderWithDetails?

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    abstract fun getAll(): Flow<List<OrderWithDetails>>

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    abstract fun paged(): PagingSource<Int, OrderWithDetails>

    @Transaction
    @Query("SELECT * FROM orders WHERE searchText LIKE '%' || :query || '%' ORDER BY createdAtMillis DESC")
    abstract fun search(query: String): PagingSource<Int, OrderWithDetails>

    /** Every filter is optional: pass null to ignore it. */
    @Transaction
    @Query(
        "SELECT * FROM orders WHERE (:accountId IS NULL OR accountId = :accountId)" +
            " AND (:repId IS NULL OR repId = :repId)" +
            " AND (:syncStatus IS NULL OR syncStatus = :syncStatus)" +
            " AND createdAtMillis BETWEEN :fromMillis AND :toMillis ORDER BY createdAtMillis DESC",
    )
    abstract fun pagedFiltered(
        accountId: String?,
        repId: String?,
        syncStatus: SyncStatus?,
        fromMillis: Long,
        toMillis: Long,
    ): PagingSource<Int, OrderWithDetails>

    @Query("DELETE FROM orders WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM orders")
    abstract suspend fun clear()

    // ---- save ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    abstract suspend fun deleteItems(orderId: String)

    @Query("UPDATE orders SET searchText = $ORDER_SEARCH_TEXT_SQL WHERE id = :id")
    abstract suspend fun refreshSearchText(id: String)

    /** Saves the order with its lines and makes its search text. Returns false when the order is already uploaded. */
    @Transaction
    open suspend fun save(order: OrderEntity, items: List<OrderItemEntity>): Boolean {
        if (getById(order.id)?.syncStatus == SyncStatus.Synced) return false
        upsert(order)
        deleteItems(order.id)
        insertItems(items)
        refreshSearchText(order.id)
        return true
    }

    /** The rep cancels an order that is not uploaded yet. */
    @Query("DELETE FROM orders WHERE id = :id AND syncStatus = 'NotSynced'")
    abstract suspend fun cancel(id: String): Int

    // ---- upload ----

    @Query("SELECT * FROM orders WHERE syncStatus = 'NotSynced'")
    abstract suspend fun getNotSynced(): List<OrderEntity>

    @Query("UPDATE orders SET syncStatus = 'Synced', backendId = :backendId WHERE id = :localId")
    abstract suspend fun markSynced(localId: String, backendId: String?): Int
}
