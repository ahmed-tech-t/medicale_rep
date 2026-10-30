package com.example.medicalrep.data.local.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.local.entity.*
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.toItem
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

fun account(id: String, name: String = id, districtId: String? = null, syncStatus: SyncStatus = SyncStatus.Synced) =
    Account(id = id, name = name, district = districtId?.let { BasicModel(it, it) }, syncStatus = syncStatus).toEntity()

/** A doctor working at [accountId]; the id is made from the account so one call per account gives different doctors. */
suspend fun AppDatabase.addDoctor(accountId: String, classCategory: String = "", specialty: String = "", id: String = "doc-$accountId") =
    doctorDao().save(
        DoctorEntity(id = id, backendId = null, name = id, classCategory = classCategory, specialty = specialty, syncStatus = SyncStatus.Synced),
        listOf(accountId),
    )

fun product(id: String, name: String = id) = Product(id = id, name = name).toItem()

fun custody(id: String, name: String = id, kind: ProductType = ProductType.Gift) =
    Custody(id = id, name = name, kind = kind).toItem()

fun inventory(repId: String, itemId: String, original: Int = 10, current: Int = original) =
    UserInventoryEntity(repId, itemId, original, current)

/** An item the rep holds [original] pieces of. */
suspend fun AppDatabase.addStock(itemId: String, original: Int = 10, type: ProductType = ProductType.Gift, repId: String = "r1") {
    repDao().save(rep(repId))
    catalogDao().saveItems(
        listOf(if (type == ProductType.Product) product(itemId) else custody(itemId, kind = type)),
        listOf(inventory(repId, itemId, original)),
    )
}

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

/** The state the screens show, read from the two stored states. */
val VisitEntity.status: VisitStatus get() = visitStatusOf(approvalStatus, executionStatus)
