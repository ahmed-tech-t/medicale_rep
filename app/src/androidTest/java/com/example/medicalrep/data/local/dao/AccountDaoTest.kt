package com.example.medicalrep.data.local.dao

import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.domain.models.BasicModel
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
        assertEquals("Clinic", dao.getById("a1")?.account?.name)
        dao.update(account("a1", "Hospital"))
        assertEquals("Hospital", dao.getById("a1")?.account?.name)
        assertEquals(1, dao.deleteById("a1"))
        assertNull(dao.getById("a1"))
        assertEquals(0, dao.deleteById("a1"))
    }

    @Test fun pagingGivesTwentyPerPageAndEnds() = runBlocking {
        dao.insertAll((1..60).map { account("a%02d".format(java.util.Locale.ROOT, it), "N%02d".format(java.util.Locale.ROOT, it)) })
        val first = dao.paged().firstPage()
        assertEquals(20, first.data.size)
        assertEquals("N01", first.data.first().account.name)
        assertNotNull(first.nextKey)
        val all = dao.paged().firstPage(100)
        assertEquals(60, all.data.size)
        assertNull(all.nextKey)
    }

    @Test fun searchPartialAndArabicForms() = runBlocking {
        dao.insertAll(listOf(account("1", "صيدلية الإيمان"), account("2", "Other")))
        assertEquals(listOf("1"), dao.search(normalizeSearch("صيدليه الايمان")).allRows().map { it.account.id })
        assertEquals(listOf("1"), dao.search("صيد").allRows().map { it.account.id })
        assertEquals(2, dao.search("").allRows().size)
        assertTrue(dao.search("nothing").allRows().isEmpty())
    }

    @Test fun searchByTypeAndCount() = runBlocking {
        dao.insertAll(listOf(
            Account("1", name = "عيادة أ", accountType = BasicModel("عيادة", "عيادة")).toEntity(),
            Account("2", name = "مستشفى", accountType = BasicModel("مستشفى", "مستشفى")).toEntity(),
        ))
        assertEquals(listOf("1"), dao.searchByType(AccountType.Clinic, "").allRows().map { it.account.id })
        assertEquals(2, dao.count(null, "").first())
        assertEquals(1, dao.count(AccountType.Hospital, "").first())
    }

    @Test fun anAccountReadsItsDistrictAndCity() = runBlocking {
        db.geoDao().upsertCities(listOf(CityEntity("c1", "Cairo")))
        db.geoDao().upsertDistricts(listOf(DistrictEntity("d1", "c1", "Nasr")))
        dao.insert(account("1", districtId = "d1"))
        dao.insert(account("2"))
        val read = dao.getById("1")!!.toDomain()
        assertEquals(listOf("Nasr", "Cairo"), listOf(read.district?.name, read.city?.name))
        assertNull(dao.getById("2")!!.toDomain().district)
    }

    @Test fun placesFilterByCityDistrictAndTypeWithBlankMeaningAny() = runBlocking {
        db.geoDao().upsertCities(listOf(CityEntity("c1", "A"), CityEntity("c2", "B")))
        db.geoDao().upsertDistricts(listOf(DistrictEntity("d1", "c1", "x"), DistrictEntity("d2", "c1", "y"), DistrictEntity("d3", "c2", "x")))
        dao.insertAll(listOf(
            Account("1", name = "a", district = BasicModel("d1", "x"), accountType = BasicModel("عيادة", "عيادة")).toEntity(),
            Account("2", name = "b", district = BasicModel("d2", "y"), accountType = BasicModel("صيدلية", "صيدلية")).toEntity(),
            Account("3", name = "c", district = BasicModel("d3", "x"), accountType = BasicModel("عيادة", "عيادة")).toEntity(),
        ))
        assertEquals(3, dao.places("", "", "").first().size)
        assertEquals(listOf("1", "2"), dao.places("c1", "", "").first().map { it.account.id })
        assertEquals(listOf("1"), dao.places("c1", "d1", "عيادة").first().map { it.account.id })
        assertEquals(emptyList<String>(), dao.places("c2", "", "صيدلية").first().map { it.account.id })
    }

    @Test fun notSyncedAndMarkSynced() = runBlocking {
        dao.insertAll(listOf(account("1", syncStatus = SyncStatus.NotSynced), account("2")))
        assertEquals(listOf("1"), dao.getNotSynced().map { it.account.id })
        dao.markSynced("1", "server-1")
        assertTrue(dao.getNotSynced().isEmpty())
        assertEquals("server-1", dao.getById("1")?.account?.backendId)
    }

    @Test fun upsertFromServerSkipsNotSyncedRows() = runBlocking {
        dao.insert(account("1", "Local edit", syncStatus = SyncStatus.NotSynced))
        dao.insert(account("2", "Old"))
        dao.upsertFromServer(listOf(account("1", "Server"), account("2", "Server"), account("3", "New")))
        assertEquals("Local edit", dao.getById("1")?.account?.name)
        assertEquals("Server", dao.getById("2")?.account?.name)
        assertEquals("New", dao.getById("3")?.account?.name)
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
