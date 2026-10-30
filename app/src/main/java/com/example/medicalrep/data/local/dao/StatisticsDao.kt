package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.ExecutionStatus
import kotlinx.coroutines.flow.Flow

/** How many accounts of one type [accountType] (null when it is not one of the kinds) are in the result. */
data class TypeCountRow(val accountType: AccountType?, val accounts: Int)

data class ClassCountRow(val grade: String, val accounts: Int)

/** The accounts of the visits planned in a time range: still to do, done, or missed. */
private const val VISITED =
    "FROM accounts a JOIN visits v ON v.accountId = a.id WHERE v.approvalStatus = 'Approved'" +
        " AND v.dateTimeMillis >= :fromMillis AND v.dateTimeMillis < :toMillis"

/** Count queries for the Statistics page. Every range is from (included) to (not included). */
@Dao
interface StatisticsDao {

    @Query("SELECT COUNT(*) FROM visits WHERE executionStatus = 'Completed' AND completedAtMillis >= :fromMillis AND completedAtMillis < :toMillis")
    fun completedBetween(fromMillis: Long, toMillis: Long): Flow<Int>

    /** The visits planned in the range: still to do, done, or missed. */
    @Query("SELECT COUNT(*) FROM visits WHERE approvalStatus = 'Approved' AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis")
    fun plannedBetween(fromMillis: Long, toMillis: Long): Flow<Int>

    /** One rep's approved visits with one execution status in the range. */
    @Query("SELECT COUNT(*) FROM visits WHERE repId = :repId AND approvalStatus = 'Approved' AND executionStatus = :status AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis")
    fun countOfRep(repId: String, status: ExecutionStatus, fromMillis: Long, toMillis: Long): Flow<Int>

    @Query("SELECT a.accountType AS accountType, COUNT(DISTINCT a.id) AS accounts $VISITED GROUP BY a.accountType")
    fun visitedAccountsByType(fromMillis: Long, toMillis: Long): Flow<List<TypeCountRow>>

    /** The doctors of the visited accounts, each counted once. */
    @Query("SELECT COUNT(DISTINCT ad.doctorId) FROM account_doctors ad WHERE ad.accountId IN (SELECT a.id $VISITED)")
    fun visitedDoctors(fromMillis: Long, toMillis: Long): Flow<Int>

    /** How many accounts have a doctor of each class; an account with doctors of two classes counts in both. */
    @Query("SELECT d.classCategory AS grade, COUNT(DISTINCT ad.accountId) AS accounts FROM doctors d JOIN account_doctors ad ON ad.doctorId = d.id WHERE d.classCategory IN ('A', 'B', 'C') GROUP BY d.classCategory ORDER BY d.classCategory")
    fun classCounts(): Flow<List<ClassCountRow>>

    @Query("SELECT COALESCE(SUM(i.original), 0) FROM user_inventory i JOIN catalog_items c ON c.id = i.itemId WHERE c.type = 'Product'")
    fun productQuantity(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM order_items")
    fun orderedQuantity(): Flow<Int>
}
