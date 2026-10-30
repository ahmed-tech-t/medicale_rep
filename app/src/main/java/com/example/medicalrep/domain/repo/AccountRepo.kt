package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.BasicModel
import kotlinx.coroutines.flow.Flow

interface AccountRepo {
    /** [type] null means every type; the filter runs in the database. */
    fun accounts(query: String = "", type: AccountType? = null): Flow<PagingData<Account>>

    /** How many accounts [accounts] would list for the same [query] and [type]. */
    fun accountCount(query: String = "", type: AccountType? = null): Flow<Int>
    suspend fun get(id: String): Account?
    suspend fun save(account: Account)
    suspend fun delete(id: String)

    /** A download never overwrites an account that has changes waiting to be uploaded. */
    suspend fun saveFromServer(accounts: List<Account>)

    /** Accounts at a place; a null [cityId], [districtId] or [entityType] means any. */
    fun places(
        cityId: String? = null,
        districtId: String? = null,
        entityType: String? = null
    ): Flow<List<Account>>

    fun entityTypes(): Flow<List<BasicModel>>

    suspend fun notSynced(): List<Account>
    suspend fun markSynced(id: String, backendId: String?)
}
