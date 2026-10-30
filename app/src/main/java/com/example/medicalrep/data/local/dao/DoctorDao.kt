package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.AccountDoctorEntity
import com.example.medicalrep.data.local.entity.DoctorEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DoctorDao : BaseDao<DoctorEntity> {

    @Query("SELECT * FROM doctors WHERE id = :id")
    abstract suspend fun getById(id: String): DoctorEntity?

    @Query("SELECT d.* FROM doctors d INNER JOIN account_doctors ad ON ad.doctorId = d.id WHERE ad.accountId = :accountId ORDER BY d.name")
    abstract fun doctorsOf(accountId: String): Flow<List<DoctorEntity>>

    @Query("SELECT DISTINCT specialty FROM doctors WHERE specialty != '' ORDER BY specialty")
    abstract fun specialties(): Flow<List<String>>

    @Query("SELECT accountId FROM account_doctors WHERE doctorId = :doctorId ORDER BY accountId")
    abstract suspend fun accountIdsOf(doctorId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun link(links: List<AccountDoctorEntity>)

    @Query("DELETE FROM account_doctors WHERE accountId = :accountId AND doctorId = :doctorId")
    abstract suspend fun unlink(accountId: String, doctorId: String): Int

    /** Saves the doctor in place and links it to every account in [accountIds]. */
    @Transaction
    open suspend fun save(doctor: DoctorEntity, accountIds: List<String>) {
        upsert(doctor)
        link(accountIds.map { AccountDoctorEntity(accountId = it, doctorId = doctor.id) })
    }
}
