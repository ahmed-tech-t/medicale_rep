package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.data.local.search.normalizeSearch

/** CRUD, paging, Arabic search, distinct filters, sync flags, download rules and the visits' search text. */
@RunWith(AndroidJUnit4::class)
class AccountDaoTest : DaoTestBase() {
    private val dao get() = db.accountDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(account("a1", "Clinic"))
        assertEquals("Clinic", dao.getById("a1")?.name)
        dao.update(account("a1", "Hospital"))
        assertEquals("Hospital", dao.getById("a1")?.name)
        assertEquals(1, dao.deleteById("a1"))
        assertNull(dao.getById("a1"))
        assertEquals(0, dao.deleteById("a1"))
    }

    @Test fun pagingGivesTwentyPerPageAndEnds() = runBlocking {
        dao.insertAll((1..60).map { account("a%02d".format(java.util.Locale.ROOT, it), "N%02d".format(java.util.Locale.ROOT, it)) })
        val first = dao.paged().firstPage()
        assertEquals(20, first.data.size)
        assertEquals("N01", first.data.first().name)
        assertNotNull(first.nextKey)
        val all = dao.paged().firstPage(100)
        assertEquals(60, all.data.size)
        assertNull(all.nextKey)
    }

    @Test fun searchPartialAndArabicForms() = runBlocking {
        dao.insertAll(listOf(account("1", "صيدلية الإيمان", "Cairo"), account("2", "Other", "Giza")))
        assertEquals(listOf("1"), dao.search(normalizeSearch("صيدليه الايمان")).allRows().map { it.id })
        assertEquals(listOf("1"), dao.search("cai").allRows().map { it.id })
        assertEquals(2, dao.search("").allRows().size)
        assertTrue(dao.search("nothing").allRows().isEmpty())
    }

    @Test fun searchByTypeAndCount() = runBlocking {
        dao.insertAll(listOf(
            Account("1", name = "عيادة أ", entityType = "عيادة").toEntity(),
            Account("2", name = "مستشفى", entityType = "مستشفى").toEntity(),
        ))
        assertEquals(listOf("1"), dao.searchByType(AccountType.Clinic, "").allRows().map { it.id })
        assertEquals(2, dao.count(null, "").first())
        assertEquals(1, dao.count(AccountType.Hospital, "").first())
    }

    @Test fun distinctValuesSkipBlank() = runBlocking {
        dao.insertAll(listOf(account("1", city = "Cairo"), account("2", city = "Cairo"), account("3", city = "")))
        assertEquals(listOf("Cairo"), dao.distinctCities().first())
    }

    @Test fun notSyncedAndMarkSynced() = runBlocking {
        dao.insertAll(listOf(account("1", syncStatus = SyncStatus.NotSynced), account("2")))
        assertEquals(listOf("1"), dao.getNotSynced().map { it.id })
        dao.markSynced("1", "server-1")
        assertTrue(dao.getNotSynced().isEmpty())
        assertEquals("server-1", dao.getById("1")?.backendId)
    }

    @Test fun upsertFromServerSkipsNotSyncedRows() = runBlocking {
        dao.insert(account("1", "Local edit", syncStatus = SyncStatus.NotSynced))
        dao.insert(account("2", "Old"))
        dao.upsertFromServer(listOf(account("1", "Server"), account("2", "Server"), account("3", "New")))
        assertEquals("Local edit", dao.getById("1")?.name)
        assertEquals("Server", dao.getById("2")?.name)
        assertEquals("New", dao.getById("3")?.name)
    }

    @Test fun deletingAccountSetsVisitAccountIdToNull() = runBlocking {
        dao.insert(account("a"))
        db.visitDao().save(visit("v", accountId = "a"), emptyList())
        dao.deleteById("a")
        assertNull(db.visitDao().getById("v")?.accountId)
    }

    @Test fun renamingAccountRefreshesItsVisitsSearchText() = runBlocking {
        dao.save(account("a", "Old Clinic"))
        db.visitDao().save(visit("v", accountId = "a"), emptyList())
        assertEquals(1, db.visitDao().search("old clinic").allRows().size)
        dao.save(account("a", "New Clinic"))
        assertEquals(1, db.visitDao().search("new clinic").allRows().size)
        assertTrue(db.visitDao().search("old clinic").allRows().isEmpty())
    }
}
