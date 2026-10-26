package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.SyncInfoDao
import com.example.medicalrep.data.local.entity.SyncInfoEntity
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.domain.models.SyncInfo
import com.example.medicalrep.domain.repo.SyncInfoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncInfoRepoImpl @Inject constructor(private val dao: SyncInfoDao) : SyncInfoRepo {

    override fun all(): Flow<List<SyncInfo>> = dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun get(key: String): SyncInfo? = dao.getById(key)?.toDomain()

    override suspend fun set(key: String, lastUpdatedMillis: Long) {
        dao.upsert(SyncInfoEntity(key, lastUpdatedMillis))
    }
}
