package com.example.medicalrep.domain.repo

import androidx.paging.PagingData
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** In-memory accounts for ViewModel tests; search matches the name, the type filter uses [AccountType.fromEntityType]. */
class FakeAccountRepo(initial: List<Account> = emptyList()) : AccountRepo {
    val current = MutableStateFlow(initial)

    /** The type asked for by every `accounts` call, in order (null = All): tells which filter reached the repo. */
    val requestedTypes = mutableListOf<AccountType?>()

    /** The search text of every `accounts` call, in order. */
    val requestedQueries = mutableListOf<String>()

    private fun filtered(query: String, type: AccountType?): Flow<List<Account>> = current.map { list ->
        list.filter { (type == null || AccountType.fromEntityType(it.entityType) == type) && it.name.contains(query, ignoreCase = true) }
    }

    override fun accounts(query: String, type: AccountType?): Flow<PagingData<Account>> {
        requestedTypes += type
        requestedQueries += query
        return filtered(query, type).map { PagingData.from(it) }
    }

    override fun accountCount(query: String, type: AccountType?): Flow<Int> = filtered(query, type).map { it.size }

    override suspend fun get(id: String): Account? = current.value.firstOrNull { it.id == id }

    override suspend fun save(account: Account) {
        current.value = current.value.filter { it.id != account.id } + account
    }

    override suspend fun delete(id: String) {
        current.value = current.value.filter { it.id != id }
    }

    override suspend fun saveFromServer(accounts: List<Account>) {
        accounts.forEach { save(it) }
    }

    override fun places(city: String, district: String, entityType: String): Flow<List<Account>> = current.map { list ->
        list.filter { (city.isBlank() || it.city == city) && (district.isBlank() || it.district == district) && (entityType.isBlank() || it.entityType == entityType) }
    }

    override fun cities(): Flow<List<String>> = current.map { list -> list.map { it.city }.filter { it.isNotBlank() }.distinct() }
    override fun districts(): Flow<List<String>> = current.map { list -> list.map { it.district }.filter { it.isNotBlank() }.distinct() }
    override fun specialties(): Flow<List<String>> = current.map { list -> list.map { it.specialty }.filter { it.isNotBlank() }.distinct() }
    override fun entityTypes(): Flow<List<String>> = current.map { list -> list.map { it.entityType }.filter { it.isNotBlank() }.distinct() }

    override suspend fun notSynced(): List<Account> = emptyList()
    override suspend fun markSynced(id: String, backendId: String?) = Unit
}
