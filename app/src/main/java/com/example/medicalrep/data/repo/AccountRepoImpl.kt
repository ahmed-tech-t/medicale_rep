package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.AccountDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.repo.AccountRepo
import javax.inject.Inject

class AccountRepoImpl @Inject constructor(private val dao: AccountDao) : AccountRepo {

    override fun accounts(query: String) = pagedFlow({ dao.search(normalizeSearch(query)) }) { it.toDomain() }

    override suspend fun get(id: String): Account? = dao.getById(id)?.toDomain()

    override suspend fun save(account: Account) = dao.save(account.toEntity())

    override suspend fun delete(id: String) {
        dao.deleteById(id)
    }

    override suspend fun saveFromServer(accounts: List<Account>) = dao.upsertFromServer(accounts.map { it.toEntity() })

    override fun cities() = dao.distinctCities()
    override fun districts() = dao.distinctDistricts()
    override fun specialties() = dao.distinctSpecialties()
    override fun entityTypes() = dao.distinctEntityTypes()

    override suspend fun notSynced(): List<Account> = dao.getNotSynced().map { it.toDomain() }

    override suspend fun markSynced(id: String, backendId: String?) {
        dao.markSynced(id, backendId)
    }
}
