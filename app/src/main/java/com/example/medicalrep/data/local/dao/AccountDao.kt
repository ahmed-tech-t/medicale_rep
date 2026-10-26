package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao : BaseDao<AccountEntity> {

    @Query("SELECT * FROM accounts WHERE id = :id")
    abstract suspend fun getById(id: String): AccountEntity?

    @Query("SELECT * FROM accounts ORDER BY name")
    abstract fun getAll(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY name")
    abstract fun paged(): PagingSource<Int, AccountEntity>

    @Query("SELECT * FROM accounts WHERE searchText LIKE '%' || :query || '%' ORDER BY name")
    abstract fun search(query: String): PagingSource<Int, AccountEntity>

    @Query("DELETE FROM accounts WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM accounts")
    abstract suspend fun clear()

    @Query("SELECT DISTINCT city FROM accounts WHERE city != '' ORDER BY city")
    abstract fun distinctCities(): Flow<List<String>>

    @Query("SELECT DISTINCT district FROM accounts WHERE district != '' ORDER BY district")
    abstract fun distinctDistricts(): Flow<List<String>>

    @Query("SELECT DISTINCT specialty FROM accounts WHERE specialty != '' ORDER BY specialty")
    abstract fun distinctSpecialties(): Flow<List<String>>

    @Query("SELECT DISTINCT entityType FROM accounts WHERE entityType != '' ORDER BY entityType")
    abstract fun distinctEntityTypes(): Flow<List<String>>

    @Query("SELECT * FROM accounts WHERE syncStatus = 'NotSynced'")
    abstract suspend fun getNotSynced(): List<AccountEntity>

    @Query("UPDATE accounts SET syncStatus = 'Synced', backendId = :backendId WHERE id = :localId")
    abstract suspend fun markSynced(localId: String, backendId: String?): Int

    @Query("SELECT id FROM accounts WHERE syncStatus = 'NotSynced' AND id IN (:ids)")
    abstract suspend fun getNotSyncedIds(ids: List<String>): List<String>

    @Query("UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE accountId = :accountId")
    abstract suspend fun refreshVisitSearchText(accountId: String)

    @Query("UPDATE orders SET searchText = $ORDER_SEARCH_TEXT_SQL WHERE accountId = :accountId")
    abstract suspend fun refreshOrderSearchText(accountId: String)

    /** Saves the account and refreshes the search text of its visits and orders. */
    @Transaction
    open suspend fun save(account: AccountEntity) {
        upsert(account)
        refreshVisitSearchText(account.id)
        refreshOrderSearchText(account.id)
    }

    /** A download never overwrites an account that has changes waiting to be uploaded. */
    @Transaction
    open suspend fun upsertFromServer(accounts: List<AccountEntity>) {
        val locked = accounts.chunked(SQLITE_IN_LIMIT).flatMap { getNotSyncedIds(it.map(AccountEntity::id)) }.toSet()
        accounts.filter { it.id !in locked }.forEach { save(it) }
    }
}

const val SQLITE_IN_LIMIT = 900
