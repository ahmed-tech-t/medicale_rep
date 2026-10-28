package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.VisitStatus
import kotlinx.coroutines.flow.Flow

/** How many accounts of one type [accountType] (null when it is not one of the kinds) are in the result. */
data class TypeCountRow(val accountType: AccountType?, val accounts: Int)

data class ClassCountRow(val grade: String, val accounts: Int)

/** The accounts of the visits planned in a time range: still to do, done, or missed. */
private const val VISITED =
    "FROM accounts a JOIN visits v ON v.accountId = a.id WHERE v.status IN ('Scheduled', 'Completed', 'Missed')" +
        " AND v.dateTimeMillis >= :fromMillis AND v.dateTimeMillis < :toMillis"

/** Count queries for the Statistics page. Every range is from (included) to (not included). */
@Dao
interface StatisticsDao {

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'Completed' AND completedAtMillis >= :fromMillis AND completedAtMillis < :toMillis")
    fun completedBetween(fromMillis: Long, toMillis: Long): Flow<Int>

    /** The visits planned in the range: still to do, done, or missed. */
    @Query("SELECT COUNT(*) FROM visits WHERE status IN ('Scheduled', 'Completed', 'Missed') AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis")
    fun plannedBetween(fromMillis: Long, toMillis: Long): Flow<Int>

    /** One rep's visits of one status in the range. */
    @Query("SELECT COUNT(*) FROM visits WHERE repId = :repId AND status = :status AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis")
    fun countOfRep(repId: String, status: VisitStatus, fromMillis: Long, toMillis: Long): Flow<Int>

    @Query("SELECT a.accountType AS accountType, COUNT(DISTINCT a.id) AS accounts $VISITED GROUP BY a.accountType")
    fun visitedAccountsByType(fromMillis: Long, toMillis: Long): Flow<List<TypeCountRow>>

    /** An account with a specialty is a doctor. */
    @Query("SELECT COUNT(DISTINCT a.id) $VISITED AND a.specialty != ''")
    fun visitedDoctors(fromMillis: Long, toMillis: Long): Flow<Int>

    @Query("SELECT classCategory AS grade, COUNT(*) AS accounts FROM accounts WHERE classCategory IN ('A', 'B', 'C') GROUP BY classCategory ORDER BY classCategory")
    fun classCounts(): Flow<List<ClassCountRow>>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM products")
    fun productQuantity(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM order_items")
    fun orderedQuantity(): Flow<Int>
}
