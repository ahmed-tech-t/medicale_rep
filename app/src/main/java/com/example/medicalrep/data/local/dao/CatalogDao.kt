package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.medicalrep.data.local.entity.CatalogItemEntity
import com.example.medicalrep.data.local.entity.UserInventoryEntity
import com.example.medicalrep.data.local.relation.CatalogItemWithStock
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.Flow

/** Every catalog item with the stock the rep holds, and how many orders sold (so a product's balance is right offline). */
private const val WITH_STOCK =
    "SELECT c.*, COALESCE(i.original, 0) AS original, COALESCE(i.`current`, 0) AS stock," +
        " COALESCE((SELECT SUM(oi.quantity) FROM order_items oi WHERE oi.productId = c.id), 0) AS sold" +
        " FROM catalog_items c LEFT JOIN user_inventory i ON i.itemId = c.id"

@Dao
interface CatalogDao : BaseDao<CatalogItemEntity> {
    @Query("$WITH_STOCK WHERE c.id = :id")
    suspend fun getById(id: String): CatalogItemWithStock?

    @Query("$WITH_STOCK WHERE c.type = :type ORDER BY c.name")
    fun ofType(type: ProductType): Flow<List<CatalogItemWithStock>>

    @Query("$WITH_STOCK WHERE c.type = :type AND c.searchText LIKE '%' || :query || '%' ORDER BY c.name")
    fun search(type: ProductType, query: String): PagingSource<Int, CatalogItemWithStock>

    @Upsert
    suspend fun upsertInventory(rows: List<UserInventoryEntity>)

    @Transaction
    suspend fun saveItems(items: List<CatalogItemEntity>, inventory: List<UserInventoryEntity>) {
        upsertAll(items)
        upsertInventory(inventory)
    }

    @Query("DELETE FROM catalog_items WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM catalog_items")
    suspend fun clear()
}
