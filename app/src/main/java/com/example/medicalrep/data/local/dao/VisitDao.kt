package com.example.medicalrep.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.entity.VisitEntity
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.Flow

/** The optional shift and account class filters the visit lists share; pass null to ignore one. */
private const val VISIT_SHIFT_AND_CLASS_FILTER =
    "(:shift IS NULL OR shift = :shift) AND (:classCategory IS NULL OR accountId IN (SELECT id FROM accounts WHERE classCategory = :classCategory))"

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
    abstract suspend fun insertLines(lines: List<VisitCustodyEntity>)

    @Query("DELETE FROM visit_custody WHERE visitId = :visitId")
    abstract suspend fun deleteLines(visitId: String)

    @Transaction
    open suspend fun replaceLines(visitId: String, lines: List<VisitCustodyEntity>) {
        deleteLines(visitId)
        insertLines(lines)
    }

    // ---- save ----

    @Query("UPDATE visits SET searchText = $VISIT_SEARCH_TEXT_SQL WHERE id = :id")
    abstract suspend fun refreshSearchText(id: String)

    /** Saves the visit with its lines and makes its search text. */
    @Transaction
    open suspend fun save(visit: VisitEntity, lines: List<VisitCustodyEntity>) {
        upsert(visit)
        replaceLines(visit.id, lines)
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
    @Query("SELECT * FROM visits WHERE status = 'PendingApproval' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY requestedAtMillis DESC")
    abstract fun pendingPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'PendingApproval' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun pendingFilteredCount(shift: Shift?, classCategory: String?): Flow<Int>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'PendingApproval'")
    abstract fun countPending(): Flow<Int>

    /** Requests the admin refused, newest first. */
    @Transaction
    @Query("SELECT * FROM visits WHERE status = 'Rejected' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY requestedAtMillis DESC")
    abstract fun rejectedPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'Rejected' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun rejectedFilteredCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** The admin's decision arrives: an approved request becomes a scheduled planned visit, a refused one is Rejected. */
    @Query(
        "UPDATE visits SET status = CASE WHEN :approved THEN 'Scheduled' ELSE 'Rejected' END, decidedAtMillis = :decidedAtMillis," +
            " type = CASE WHEN :approved THEN 'Planned' ELSE type END WHERE backendId = :backendId AND status = 'PendingApproval'",
    )
    abstract suspend fun applyDecision(backendId: String, approved: Boolean, decidedAtMillis: Long): Int

    /** The rep dismisses a rejected request. */
    @Query("DELETE FROM visits WHERE id = :id AND status = 'Rejected'")
    abstract suspend fun dismissRejected(id: String): Int

    /** The rep cancels a request that was not uploaded yet. */
    @Query("DELETE FROM visits WHERE id = :id AND status = 'PendingApproval' AND syncStatus = 'NotSynced'")
    abstract suspend fun cancelRequest(id: String): Int

    // ---- lists by status ----

    /** Visits still to do. Every filter is optional (null = ignore). */
    @Transaction
    @Query("SELECT * FROM visits WHERE status = 'Scheduled' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY dateTimeMillis")
    abstract fun requiredPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'Scheduled' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun requiredCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** Visits whose day passed without the visit, most recent first. */
    @Transaction
    @Query("SELECT * FROM visits WHERE status = 'Missed' AND $VISIT_SHIFT_AND_CLASS_FILTER ORDER BY dateTimeMillis DESC")
    abstract fun missedPaged(shift: Shift?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'Missed' AND $VISIT_SHIFT_AND_CLASS_FILTER")
    abstract fun missedCount(shift: Shift?, classCategory: String?): Flow<Int>

    /** Scheduled visits dated before [beforeMillis] (a day start) were not done: they become Missed. A visit with no date is left alone. */
    @Query("UPDATE visits SET status = 'Missed' WHERE status = 'Scheduled' AND dateTimeMillis > 0 AND dateTimeMillis < :beforeMillis")
    abstract suspend fun markMissed(beforeMillis: Long): Int

    /** Finished visits, newest first. [doubleOnly] keeps only double visits; the other filters are optional. */
    @Transaction
    @Query("SELECT * FROM visits WHERE status = 'Completed' AND (:shift IS NULL OR shift = :shift) AND (:doubleOnly = 0 OR visitMode = 'Double') AND (:syncStatus IS NULL OR syncStatus = :syncStatus) AND (:classCategory IS NULL OR accountId IN (SELECT id FROM accounts WHERE classCategory = :classCategory)) ORDER BY completedAtMillis DESC")
    abstract fun donePaged(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): PagingSource<Int, VisitWithDetails>

    @Query("SELECT COUNT(*) FROM visits WHERE status = 'Completed' AND (:shift IS NULL OR shift = :shift) AND (:doubleOnly = 0 OR visitMode = 'Double') AND (:syncStatus IS NULL OR syncStatus = :syncStatus) AND (:classCategory IS NULL OR accountId IN (SELECT id FROM accounts WHERE classCategory = :classCategory))")
    abstract fun doneCount(shift: Shift?, doubleOnly: Boolean, syncStatus: SyncStatus?, classCategory: String?): Flow<Int>

    /** Only scheduled visits can be started. */
    @Transaction
    @Query("SELECT * FROM visits WHERE status = 'Scheduled' ORDER BY dateTimeMillis DESC")
    abstract fun startablePaged(): PagingSource<Int, VisitWithDetails>
}
