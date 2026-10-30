package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.relation.AccountWithPlace
import com.example.medicalrep.domain.models.AccountType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao : BaseDao<AccountEntity> {

    @Transaction
    @Query("SELECT * FROM accounts WHERE id = :id")
    abstract suspend fun getById(id: String): AccountWithPlace?

    @Transaction
    @Query("SELECT * FROM accounts ORDER BY name")
    abstract fun getAll(): Flow<List<AccountWithPlace>>

    @Transaction
    @Query("SELECT * FROM accounts ORDER BY name")
    abstract fun paged(): PagingSource<Int, AccountWithPlace>

    @Transaction
    @Query("SELECT * FROM accounts WHERE searchText LIKE '%' || :query || '%' ORDER BY name")
    abstract fun search(query: String): PagingSource<Int, AccountWithPlace>

    @Transaction
    @Query("SELECT * FROM accounts WHERE accountType = :type AND searchText LIKE '%' || :query || '%' ORDER BY name")
    abstract fun searchByType(type: AccountType, query: String): PagingSource<Int, AccountWithPlace>

    /** [type] null counts every type. */
    @Query("SELECT COUNT(*) FROM accounts WHERE (:type IS NULL OR accountType = :type) AND searchText LIKE '%' || :query || '%'")
    abstract fun count(type: AccountType?, query: String): Flow<Int>

    /** Accounts at a place; a blank city id, district id or type means any. */
    @Transaction
    @Query(
        "SELECT * FROM accounts WHERE (:cityId = '' OR districtId IN (SELECT id FROM districts WHERE cityId = :cityId))" +
            " AND (:districtId = '' OR districtId = :districtId) AND (:entityType = '' OR entityType = :entityType) ORDER BY name",
    )
    abstract fun places(cityId: String, districtId: String, entityType: String): Flow<List<AccountWithPlace>>

    @Query("DELETE FROM accounts WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM accounts")
    abstract suspend fun clear()

    @Query("SELECT DISTINCT entityType FROM accounts WHERE entityType != '' ORDER BY entityType")
    abstract fun distinctEntityTypes(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM accounts WHERE syncStatus = 'NotSynced'")
    abstract suspend fun getNotSynced(): List<AccountWithPlace>

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
