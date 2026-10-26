package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.CustodyEntity
import com.example.medicalrep.data.local.relation.CustodyWithPending
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.Flow

/** Every custody row with the quantity given in visits that are not uploaded yet. */
private const val WITH_PENDING =
    "SELECT c.*, COALESCE((SELECT SUM(vc.quantity) FROM visit_custody vc" +
        " JOIN visits v ON v.id = vc.visitId WHERE vc.custodyId = c.id AND v.syncStatus = 'NotSynced'), 0) AS pending" +
        " FROM custody c"

@Dao
interface CustodyDao : BaseDao<CustodyEntity> {

    @Query("SELECT * FROM custody WHERE id = :id")
    suspend fun getById(id: String): CustodyEntity?

    @Query("$WITH_PENDING WHERE c.id = :id")
    suspend fun getWithPending(id: String): CustodyWithPending?

    @Query("$WITH_PENDING ORDER BY c.name")
    fun getAll(): Flow<List<CustodyWithPending>>

    @Query("$WITH_PENDING ORDER BY c.name")
    fun paged(): PagingSource<Int, CustodyWithPending>

    @Query("$WITH_PENDING WHERE c.kind = :kind ORDER BY c.name")
    fun pagedByKind(kind: ProductType): PagingSource<Int, CustodyWithPending>

    @Query("$WITH_PENDING WHERE c.searchText LIKE '%' || :query || '%' ORDER BY c.name")
    fun search(query: String): PagingSource<Int, CustodyWithPending>

    @Query("$WITH_PENDING WHERE c.kind = :kind AND c.searchText LIKE '%' || :query || '%' ORDER BY c.name")
    fun searchByKind(kind: ProductType, query: String): PagingSource<Int, CustodyWithPending>

    @Query("DELETE FROM custody WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM custody")
    suspend fun clear()
}
