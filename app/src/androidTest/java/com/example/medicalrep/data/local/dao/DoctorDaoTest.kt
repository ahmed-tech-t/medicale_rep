package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.entity.DoctorEntity
import com.example.medicalrep.domain.models.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

/** The doctor-account link is many-to-many: one doctor at several accounts, several doctors at one account. */
@RunWith(AndroidJUnit4::class)
class DoctorDaoTest : DaoTestBase() {
    private val dao get() = db.doctorDao()

    private fun doctor(id: String, name: String = id) =
        DoctorEntity(id = id, backendId = null, name = name, classCategory = "A", specialty = "", syncStatus = SyncStatus.NotSynced)

    @Test fun anAccountListsItsDoctorsByName() = runBlocking {
        db.accountDao().insertAll(listOf(account("a1"), account("a2")))
        dao.save(doctor("d2", "Tarek"), listOf("a1"))
        dao.save(doctor("d1", "Yasmin"), listOf("a1", "a2"))
        assertEquals(listOf("Tarek", "Yasmin"), dao.doctorsOf("a1").first().map { it.name })
        assertEquals(listOf("Yasmin"), dao.doctorsOf("a2").first().map { it.name })
    }

    @Test fun oneDoctorWorksAtSeveralAccounts() = runBlocking {
        db.accountDao().insertAll(listOf(account("a1"), account("a2")))
        dao.save(doctor("d1"), listOf("a1", "a2"))
        assertEquals(listOf("a1", "a2"), dao.accountIdsOf("d1"))
    }

    @Test fun linkingTwiceKeepsOneLink() = runBlocking {
        db.accountDao().insert(account("a1"))
        dao.save(doctor("d1"), listOf("a1"))
        dao.save(doctor("d1", "Renamed"), listOf("a1"))
        assertEquals(listOf("Renamed"), dao.doctorsOf("a1").first().map { it.name })
    }

    @Test fun unlinkingKeepsTheDoctorAndItsOtherAccounts() = runBlocking {
        db.accountDao().insertAll(listOf(account("a1"), account("a2")))
        dao.save(doctor("d1"), listOf("a1", "a2"))
        assertEquals(1, dao.unlink("a1", "d1"))
        assertEquals(emptyList<String>(), dao.doctorsOf("a1").first().map { it.id })
        assertEquals(listOf("d1"), dao.doctorsOf("a2").first().map { it.id })
        assertNotNull(dao.getById("d1"))
    }

    @Test fun deletingAnAccountRemovesOnlyItsLinks() = runBlocking {
        db.accountDao().insertAll(listOf(account("a1"), account("a2")))
        dao.save(doctor("d1"), listOf("a1", "a2"))
        db.accountDao().deleteById("a1")
        assertEquals(listOf("a2"), dao.accountIdsOf("d1"))
        assertNotNull(dao.getById("d1"))
    }

    @Test fun deletingADoctorRemovesItFromEveryAccount() = runBlocking {
        db.accountDao().insert(account("a1"))
        dao.save(doctor("d1"), listOf("a1"))
        dao.delete(doctor("d1"))
        assertNull(dao.getById("d1"))
        assertEquals(emptyList<String>(), dao.doctorsOf("a1").first().map { it.id })
    }
}
