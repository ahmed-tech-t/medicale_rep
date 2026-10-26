package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao : BaseDao<ProductEntity> {

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY name")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name")
    fun paged(): PagingSource<Int, ProductEntity>

    @Query("SELECT * FROM products WHERE searchText LIKE '%' || :query || '%' ORDER BY name")
    fun search(query: String): PagingSource<Int, ProductEntity>

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM products")
    suspend fun clear()
}
