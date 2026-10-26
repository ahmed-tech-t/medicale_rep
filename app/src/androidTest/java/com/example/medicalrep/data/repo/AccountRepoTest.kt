package com.example.medicalrep.data.repo

import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.dao.DaoTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.*

/** Round trip, paging, Arabic search, filters, download rule and upload flags. */
@RunWith(AndroidJUnit4::class)
class AccountRepoTest : RepoTestBase() {
    private val repo get() = AccountRepoImpl(db.accountDao())

    @Test fun saveGetDelete() = runTest {
        repo.save(Account("a1", name = "Clinic"))
        assertEquals("Clinic", repo.get("a1")?.name)
        repo.delete("a1")
        assertNull(repo.get("a1"))
    }

    @Test fun pagingAndSearch() = runTest {
        (1..60).forEach { repo.save(Account("a$it", name = "N${"%02d".format(java.util.Locale.ROOT, it)}")) }
        repo.save(Account("x", name = "مدرسة"))
        assertTrue(repo.accounts().asSnapshot().size in 20..40)
        assertEquals(listOf("x"), repo.accounts("مدرسه").asSnapshot().map { it.id })
        assertTrue(repo.accounts("zzz").asSnapshot().isEmpty())
    }

    @Test fun typeFilterAndCountRunInTheDatabase() = runTest {
        repo.save(Account("1", name = "عيادة أ", entityType = "عيادة"))
        repo.save(Account("2", name = "عيادة ب", entityType = "عيادة"))
        repo.save(Account("3", name = "مستشفى", entityType = "مستشفى"))
        assertEquals(setOf("1", "2"), repo.accounts(type = AccountType.Clinic).asSnapshot().map { it.id }.toSet())
        assertEquals(listOf("2"), repo.accounts("ب", AccountType.Clinic).asSnapshot().map { it.id })
        assertEquals(3, repo.accountCount().first())
        assertEquals(2, repo.accountCount(type = AccountType.Clinic).first())
        assertEquals(0, repo.accountCount(type = AccountType.Pharmacy).first())
    }

    @Test fun placesAreFilteredByCityDistrictAndTypeWithBlankMeaningAny() = runTest {
        repo.save(Account("1", name = "أ", city = "منوف", district = "وسط البلد", entityType = "عيادة"))
        repo.save(Account("2", name = "ب", city = "منوف", district = "البر الشرقى", entityType = "صيدلية"))
        repo.save(Account("3", name = "ج", city = "قويسنا", district = "وسط البلد", entityType = "عيادة"))
        assertEquals(3, repo.places().first().size)
        assertEquals(listOf("أ", "ب"), repo.places(city = "منوف").first().map { it.name })
        assertEquals(listOf("أ"), repo.places("منوف", "وسط البلد", "عيادة").first().map { it.name })
        assertEquals(emptyList<String>(), repo.places("قويسنا", "", "صيدلية").first().map { it.name })
    }

    @Test fun distinctValues() = runTest {
        repo.save(Account("1", name = "a", city = "Cairo", specialty = "Cardio"))
        repo.save(Account("2", name = "b", city = "Cairo"))
        assertEquals(listOf("Cairo"), repo.cities().first())
        assertEquals(listOf("Cardio"), repo.specialties().first())
    }

    @Test fun downloadDoesNotOverwriteLocalEdits() = runTest {
        repo.save(Account("1", name = "Local", syncStatus = SyncStatus.NotSynced))
        repo.saveFromServer(listOf(Account("1", name = "Server", syncStatus = SyncStatus.Synced), Account("2", name = "New", syncStatus = SyncStatus.Synced)))
        assertEquals("Local", repo.get("1")?.name)
        assertEquals("New", repo.get("2")?.name)
    }

    @Test fun notSyncedAndMarkSynced() = runTest {
        repo.save(Account("1", name = "a", syncStatus = SyncStatus.NotSynced))
        assertEquals(listOf("1"), repo.notSynced().map { it.id })
        repo.markSynced("1", "srv")
        assertTrue(repo.notSynced().isEmpty())
        assertEquals("srv", repo.get("1")?.backendId)
    }
}
