package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.UserEntity
import com.example.medicalrep.domain.models.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users WHERE id = :id")
    abstract suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY displayName")
    abstract fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY displayName")
    abstract fun getByRole(role: UserRole): Flow<List<UserEntity>>

    /** The reps of one supervisor. */
    @Query("SELECT * FROM users WHERE supervisorId = :supervisorId ORDER BY displayName")
    abstract fun getRepsOf(supervisorId: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE searchText LIKE '%' || :query || '%' ORDER BY displayName")
    abstract fun search(query: String): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM users WHERE role = :role AND searchText LIKE '%' || :query || '%' ORDER BY displayName")
    abstract fun searchByRole(role: UserRole, query: String): PagingSource<Int, UserEntity>

    @Query("DELETE FROM users WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM users")
    abstract suspend fun clear()

    @Query("UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE supervisorId = :userId")
    abstract suspend fun refreshVisitSearchText(userId: String)

    /** Saves the user and refreshes the search text of the visits he supervises. */
    @Transaction
    open suspend fun save(user: UserEntity) {
        upsert(user)
        refreshVisitSearchText(user.id)
    }
}
