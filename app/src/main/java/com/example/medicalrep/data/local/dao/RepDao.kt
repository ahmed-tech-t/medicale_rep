package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.RepEntity
import com.example.medicalrep.domain.models.RepRole
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RepDao : BaseDao<RepEntity> {

    @Query("SELECT * FROM reps WHERE id = :id")
    abstract suspend fun getById(id: String): RepEntity?

    @Query("SELECT * FROM reps ORDER BY displayName")
    abstract fun getAll(): Flow<List<RepEntity>>

    @Query("SELECT * FROM reps WHERE role = :role ORDER BY displayName")
    abstract fun getByRole(role: RepRole): Flow<List<RepEntity>>

    /** The reps of one supervisor. */
    @Query("SELECT * FROM reps WHERE supervisorId = :supervisorId ORDER BY displayName")
    abstract fun getRepsOf(supervisorId: String): Flow<List<RepEntity>>

    @Query("SELECT * FROM reps WHERE searchText LIKE '%' || :query || '%' ORDER BY displayName")
    abstract fun search(query: String): PagingSource<Int, RepEntity>

    @Query("SELECT * FROM reps WHERE role = :role AND searchText LIKE '%' || :query || '%' ORDER BY displayName")
    abstract fun searchByRole(role: RepRole, query: String): PagingSource<Int, RepEntity>

    @Query("DELETE FROM reps WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM reps")
    abstract suspend fun clear()

    @Query("UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE accompanyingRepId = :userId")
    abstract suspend fun refreshVisitSearchText(userId: String)

    @Query("UPDATE orders SET searchText = $ORDER_SEARCH_TEXT_SQL WHERE repId = :userId")
    abstract suspend fun refreshOrderSearchText(userId: String)

    /** Saves the user and refreshes the search text of the visits he supervises and the orders he took. */
    @Transaction
    open suspend fun save(user: RepEntity) {
        upsert(user)
        refreshVisitSearchText(user.id)
        refreshOrderSearchText(user.id)
    }
}
