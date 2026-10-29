package com.example.medicalrep.data.local.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.local.entity.*
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.search.searchTextOf
import com.example.medicalrep.domain.models.*
import org.junit.After
import org.junit.Before

/** An in-memory database for every DAO test, and small builders so a test names only what it cares about. */
abstract class DaoTestBase {
    protected lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), AppDatabase::class.java).build()
    }

    @After
    fun closeDb() = db.close()
}

fun rep(id: String, name: String = id, role: RepRole = RepRole.Rep, supervisorId: String? = null) =
    Rep(id = id, displayName = name, role = role, supervisorId = supervisorId).toEntity()

fun account(id: String, name: String = id, city: String = "", syncStatus: SyncStatus = SyncStatus.Synced, classCategory: String = "") =
    Account(id = id, name = name, city = city.opt(), syncStatus = syncStatus, classCategory = classCategory.opt()).toEntity()

fun product(id: String, name: String = id) = Product(id = id, name = name).toEntity()

fun custody(id: String, name: String = id, kind: ProductType = ProductType.Gift, received: Int = 10, dispatched: Int = 0) =
    Custody(id = id, name = name, kind = kind, received = received, dispatched = dispatched).toEntity()

fun visit(
    id: String,
    accountId: String? = null,
    repId: String? = null,
    supervisorId: String? = null,
    type: VisitType = VisitType.Unplanned,
    syncStatus: SyncStatus = SyncStatus.Synced,
    dateTimeMillis: Long = 1_700_000_000_000L,
    backendId: String? = null,
    status: VisitStatus = VisitStatus.Scheduled,
    requestedAtMillis: Long? = null,
    shift: Shift = Shift.Am,
    double: Boolean = false,
    completedAtMillis: Long? = null,
) = Visit(
    id = id, accountId = accountId, repId = repId, supervisorId = supervisorId, type = type, syncStatus = syncStatus,
    dateTimeMillis = dateTimeMillis, backendId = backendId, status = status, requestedAtMillis = requestedAtMillis,
    shift = shift, visitMode = if (double) VisitMode.Double else VisitMode.Single, completedAtMillis = completedAtMillis,
).toEntity()

fun point(repId: String, timeMillis: Long, visitId: String? = null, syncStatus: SyncStatus = SyncStatus.NotSynced) =
    TrackPointEntity(0, null, repId, visitId, 1.0, 2.0, timeMillis, syncStatus)

/** Loads the first page and returns its rows with the key of the next page. */
suspend fun <T : Any> PagingSource<Int, T>.firstPage(pageSize: Int = 20): PagingSource.LoadResult.Page<Int, T> =
    load(PagingSource.LoadParams.Refresh(null, pageSize, false)) as PagingSource.LoadResult.Page

suspend fun <T : Any> PagingSource<Int, T>.allRows(): List<T> = firstPage(1000).data

/** Test text as an account option: the text is also the id; blank means not set. */
fun String.opt() = takeIf { it.isNotBlank() }?.let { BasicModel(it, it) }
