package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.SystemSettingDao
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.SystemSetting
import com.example.medicalrep.domain.repo.SystemSettingRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SystemSettingRepoImpl @Inject constructor(private val dao: SystemSettingDao) : SystemSettingRepo {

    override fun get(key: String): Flow<String?> = dao.get(key)

    override suspend fun set(key: String, value: String) {
        dao.upsert(SystemSetting(key, value).toEntity())
    }
}
