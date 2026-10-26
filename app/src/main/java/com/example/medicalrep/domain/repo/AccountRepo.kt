package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepo {
    fun accounts(query: String = ""): Flow<PagingData<Account>>
    suspend fun get(id: String): Account?
    suspend fun save(account: Account)
    suspend fun delete(id: String)

    /** A download never overwrites an account that has changes waiting to be uploaded. */
    suspend fun saveFromServer(accounts: List<Account>)

    fun cities(): Flow<List<String>>
    fun districts(): Flow<List<String>>
    fun specialties(): Flow<List<String>>
    fun entityTypes(): Flow<List<String>>

    suspend fun notSynced(): List<Account>
    suspend fun markSynced(id: String, backendId: String?)
}
