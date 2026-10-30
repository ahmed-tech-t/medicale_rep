package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.entity.VisitEntity
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow

/** The accounts that have a doctor of the class `:classCategory`. */
internal const val CLASS_ACCOUNTS_SQL =
    "SELECT ad.accountId FROM account_doctors ad JOIN doctors d ON d.id = ad.doctorId WHERE d.classCategory = :classCategory"

/** The optional shift and account class filters the visit lists share; pass null to ignore one. */
private const val VISIT_SHIFT_AND_CLASS_FILTER =
    "(:shift IS NULL OR shift = :shift) AND (:classCategory IS NULL OR accountId IN ($CLASS_ACCOUNTS_SQL))"

@Dao
abstract class VisitDao : BaseDao<VisitEntity> {

    // ---- read ----

    @Query("SELECT * FROM visits WHERE id = :id")
    abstract suspend fun getById(id: String): VisitEntity?

    @Transaction
    @Query("SELECT * FROM visits WHERE id = :id")
    abstract suspend fun getWithDetails(id: String): VisitWithDetails?

    @Transaction
    @Query("SELECT * FROM visits ORDER BY dateTimeMillis DESC")
    abstract fun getAll(): Flow<List<VisitWithDetails>>

    @Transaction
    @Query("SELECT * FROM visits ORDER BY dateTimeMillis DESC")
    abstract fun paged(): PagingSource<Int, VisitWithDetails>

    @Transaction
    @Query("SELECT * FROM visits WHERE searchText LIKE '%' || :query || '%' ORDER BY dateTimeMillis DESC")
    abstract fun search(query: String): PagingSource<Int, VisitWithDetails>

    /** Every filter is optional: pass null to ignore it. */
    @Transaction
    @Query(
        "SELECT * FROM visits WHERE (:type IS NULL OR type = :type)" +
            " AND (:syncStatus IS NULL OR syncStatus = :syncStatus)" +
            " AND (:repId IS NULL OR repId = :repId)" +
            " AND dateTimeMillis BETWEEN :fromMillis AND :toMillis ORDER BY dateTimeMillis DESC",
    )
    abstract fun pagedFiltered(
        type: VisitType?,
        syncStatus: SyncStatus?,
        repId: String?,
        fromMillis: Long,
        toMillis: Long,
    ): PagingSource<Int, VisitWithDetails>

    // ---- delete ----

    @Query("DELETE FROM visits WHERE id = :id")
    abstract suspend fun deleteById(id: String): Int

    @Query("DELETE FROM visits")
    abstract suspend fun clear()

    // ---- samples and gifts given ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLines(lines: List<VisitItemEntity>)

    @Query("SELECT * FROM visit_items WHERE visitId = :visitId")
    abstract suspend fun linesOf(visitId: String): List<VisitItemEntity>

    @Query("DELETE FROM visit_items WHERE visitId = :visitId")
    abstract suspend fun deleteLines(visitId: String)

    /** Takes [used] pieces out of the stock (a negative number gives pieces back); the stock stays between 0 and what the rep got. */
    @Query("UPDATE user_inventory SET `current` = MIN(original, MAX(`current` - :used, 0)) WHERE itemId = :itemId")
    abstract suspend fun useStock(itemId: String, used: Int)

    @Transaction
    open suspend fun replaceLines(visitId: String, lines: List<VisitItemEntity>) {
        deleteLines(visitId)
        insertLines(lines)
    }

    // ---- doctors seen ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDoctors(links: List<VisitDoctorEntity>)

    @Query("DELETE FROM visit_doctors WHERE visitId = :visitId")
    abstract suspend fun deleteDoctors(visitId: String)

    @Transaction
    open suspend fun replaceDoctors(visitId: String, links: List<VisitDoctorEntity>) {
        deleteDoctors(visitId)
        insertDoctors(links)
    }

    // ---- save ----

    @Query("UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE id = :id")
    abstract suspend fun refreshSearchText(id: String)

    /**
     * Saves the visit with its lines and doctors and makes its search text. A visit not uploaded yet takes what it gave out of the
     * stock (only the change when it is saved again); an uploaded one is already counted by the server.
     */
    @Transaction
    open suspend fun save(visit: VisitEntity, lines: List<VisitItemEntity>, doctors: List<VisitDoctorEntity> = emptyList()) {
        if (visit.syncStatus == SyncStatus.NotSynced) {
            val before = linesOf(visit.id).associate { it.itemId to it.quantity }
            val after = lines.associate { it.itemId to it.quantity }
            (before.keys + after.keys).forEach { itemId ->
                val change = (after[itemId] ?: 0) - (before[itemId] ?: 0)
                if (change != 0) useStock(itemId, change)
            }
        }
        upsert(visit)
        replaceLines(visit.id, lines)
        replaceDoctors(visit.id, doctors)
        refreshSearchText(visit.id)
    }

    // ---- upload and download ----

    @Query("SELECT * FROM visits WHERE syncStatus = 'NotSynced'")
    abstract suspend fun getNotSynced(): List<VisitEntity>

    @Query("UPDATE visits SET syncStatus = 'Synced', backendId = :backendId WHERE id = :localId")
    abstract suspend fun markSynced(localId: String, backendId: String?): Int

    @Query("SELECT id FROM visits WHERE syncStatus = 'NotSynced' AND id IN (:ids)")
    abstract suspend fun getNotSyncedIds(ids: List<String>): List<String>

    /** A download never overwrites a visit that has changes waiting to be uploaded. */
    @Transaction
    open suspend fun upsertFromServer(visits: List<VisitEntity>) {
        val locked = visits.chunked(SQLITE_IN_LIMIT).flatMap { getNotSyncedIds(it.map(VisitEntity::id)) }.toSet()
        visits.filter { it.id !in locked }.forEach {
            upsert(it)
            refreshSearchText(it.id)
        }
    }

    // ---- plan requests ----

    /** Requests the admin has not answered yet, newest first. */
    @Transaction
    @Query("SELECT * FROM visits WHERE approvalStatus = 'Pending' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY requestedAtMillis DESC")
    abstract fun pendingPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE approvalStatus = 'Pending' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun pendingFilteredCount(shift: Shift?, classCategory: String?): Flow<Int>

    @Query("SELECT COUNT(*) FROM visits WHERE approvalStatus = 'Pending'")
    abstract fun countPending(): Flow<Int>

    /** Requests the admin refused, newest first. */
    @Transaction
    @Query("SELECT * FROM visits WHERE approvalStatus = 'Rejected' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY requestedAtMillis DESC")
    abstract fun rejectedPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE approvalStatus = 'Rejected' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun rejectedFilteredCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** The admin's decision arrives: an approved request becomes a scheduled planned visit, a refused one is Rejected. */
    @Query(
        "UPDATE visits SET approvalStatus = CASE WHEN :approved THEN 'Approved' ELSE 'Rejected' END, decidedAtMillis = :decidedAtMillis," +
            " type = CASE WHEN :approved THEN 'Planned' ELSE type END WHERE backendId = :backendId AND approvalStatus = 'Pending'",
    )
    abstract suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long): Int

    /** The rep dismisses a rejected request. */
    @Query("DELETE FROM visits WHERE id = :id AND approvalStatus = 'Rejected'")
    abstract suspend fun dismissRejected(id: String): Int

    /** The rep cancels a request that was not uploaded yet. */
    @Query("DELETE FROM visits WHERE id = :id AND approvalStatus = 'Pending' AND syncStatus = 'NotSynced'")
    abstract suspend fun cancelRequest(id: String): Int

    // ---- lists by status ----

    /** Visits still to do. Every filter is optional (null = ignore). */
    @Transaction
    @Query("SELECT * FROM visits WHERE approvalStatus = 'Approved' AND executionStatus = 'Scheduled' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY dateTimeMillis")
    abstract fun requiredPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE approvalStatus = 'Approved' AND executionStatus = 'Scheduled' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun requiredCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** Visits whose day passed without the visit, most recent first. */
    @Transaction
    @Query("SELECT * FROM visits WHERE executionStatus = 'Cancelled' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY dateTimeMillis DESC")
    abstract fun missedPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE executionStatus = 'Cancelled' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun missedCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** Scheduled visits dated before [beforeMillis] (a day start) were not done: they become Missed. A visit with no date is left alone. */
    @Query("UPDATE visits SET executionStatus = 'Cancelled' WHERE approvalStatus = 'Approved' AND executionStatus = 'Scheduled' AND dateTimeMillis > 0 AND dateTimeMillis < :beforeMillis")
    abstract suspend fun markMissed(beforeMillis: Long): Int

    /** Finished visits, newest first. [doubleOnly] keeps only double visits; the other filters are optional. */
    @Transaction
    @Query("SELECT * FROM visits WHERE executionStatus = 'Completed' AND (:shift IS NULL OR shift = :shift) AND (:doubleOnly = 0 OR visitMode = 'Double') AND (:syncStatus IS NULL OR syncStatus = :syncStatus) AND (:classCategory IS NULL OR accountId IN ($CLASS_ACCOUNTS_SQL)) ORDER BY completedAtMillis DESC")
    abstract fun donePaged(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE executionStatus = 'Completed' AND (:shift IS NULL OR shift = :shift) AND (:doubleOnly = 0 OR visitMode = 'Double') AND (:syncStatus IS NULL OR syncStatus = :syncStatus) AND (:classCategory IS NULL OR accountId IN ($CLASS_ACCOUNTS_SQL))")
    abstract fun doneCount(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): Flow<Int>

    /** The visits still to do in a day (from included, to not included): soonest first, at most [limit]. */
    @Transaction
    @Query("SELECT * FROM visits WHERE approvalStatus = 'Approved' AND executionStatus = 'Scheduled' AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis ORDER BY dateTimeMillis LIMIT :limit")
    abstract fun scheduledBetween(fromMillis: Long, toMillis: Long, limit: Int): Flow<List<VisitWithDetails>>

    /** The visits one rep finished in a range (from included, to not included), in time order. */
    @Transaction
    @Query("SELECT * FROM visits WHERE executionStatus = 'Completed' AND repId = :repId AND dateTimeMillis >= :fromMillis AND dateTimeMillis < :toMillis ORDER BY dateTimeMillis")
    abstract fun doneBetween(repId: String, fromMillis: Long, toMillis: Long): Flow<List<VisitWithDetails>>

    /** Only scheduled visits can be started. */
    @Transaction
    @Query("SELECT * FROM visits WHERE approvalStatus = 'Approved' AND executionStatus = 'Scheduled' ORDER BY dateTimeMillis DESC")
    abstract fun startablePaged(): PagingSource<Int, VisitWithDetails>
}
