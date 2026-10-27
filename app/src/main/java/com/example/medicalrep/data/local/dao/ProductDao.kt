package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.ProductEntity
import com.example.medicalrep.data.local.relation.ProductWithSold
import kotlinx.coroutines.flow.Flow

/** Every product with how many were sold, so what is left is right while orders are not uploaded yet. */
private const val WITH_SOLD =
    "SELECT p.*, COALESCE((SELECT SUM(oi.quantity) FROM order_items oi WHERE oi.productId = p.id), 0) AS sold FROM products p"

@Dao
interface ProductDao : BaseDao<ProductEntity> {

    @Query("$WITH_SOLD WHERE p.id = :id")
    suspend fun getById(id: String): ProductWithSold?

    @Query("$WITH_SOLD ORDER BY p.name")
    fun getAll(): Flow<List<ProductWithSold>>

    @Query("$WITH_SOLD ORDER BY p.name")
    fun paged(): PagingSource<Int, ProductWithSold>

    @Query("$WITH_SOLD WHERE p.searchText LIKE '%' || :query || '%' ORDER BY p.name")
    fun search(query: String): PagingSource<Int, ProductWithSold>

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM products")
    suspend fun clear()
}
