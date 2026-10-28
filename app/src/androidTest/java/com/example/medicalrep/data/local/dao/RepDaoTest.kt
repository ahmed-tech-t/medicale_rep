package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType

/** CRUD, queries by role and supervisor, Arabic search, and save refreshing the search text of supervised visits. */
@RunWith(AndroidJUnit4::class)
class RepDaoTest : DaoTestBase() {
    private val dao get() = db.repDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(rep("u1", "Ali"))
        assertEquals("Ali", dao.getById("u1")?.displayName)
        dao.update(rep("u1", "Omar"))
        assertEquals("Omar", dao.getById("u1")?.displayName)
        assertEquals(1, dao.deleteById("u1"))
        assertNull(dao.getById("u1"))
    }

    @Test fun getAllAndClear() = runBlocking {
        dao.insertAll(listOf(rep("a", "B"), rep("b", "A")))
        assertEquals(listOf("b", "a"), dao.getAll().first().map { it.id })
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test fun byRoleAndRepsOfSupervisor() = runBlocking {
        dao.insertAll(listOf(rep("s", role = RepRole.Supervisor), rep("r1", supervisorId = null)))
        dao.insert(rep("r2", supervisorId = "s"))
        assertEquals(listOf("s"), dao.getByRole(RepRole.Supervisor).first().map { it.id })
        assertEquals(listOf("r2"), dao.getRepsOf("s").first().map { it.id })
        assertTrue(dao.getRepsOf("none").first().isEmpty())
    }

    @Test fun searchPartialCaseAndArabic() = runBlocking {
        dao.insertAll(listOf(rep("1", "Ahmed Ali"), rep("2", "أحمد")))
        assertEquals(listOf("1"), dao.search("ahm").allRows().map { it.id })
        assertEquals(listOf("1", "2"), dao.search("").allRows().map { it.id })
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun searchByRoleOnlyReturnsThatRole() = runBlocking {
        dao.insertAll(listOf(rep("s", "Sam", RepRole.Supervisor), rep("r", "Sam", RepRole.Rep)))
        assertEquals(listOf("s"), dao.searchByRole(RepRole.Supervisor, "sam").allRows().map { it.id })
    }

    @Test fun deletingSupervisorClearsRepsSupervisorId() = runBlocking {
        dao.insert(rep("s", role = RepRole.Supervisor))
        dao.insert(rep("r", supervisorId = "s"))
        dao.deleteById("s")
        assertNull(dao.getById("r")?.supervisorId)
    }

    @Test fun saveRefreshesSearchTextOfSupervisedVisits() = runBlocking {
        dao.save(rep("s", "Old Name", RepRole.Supervisor))
        db.visitDao().save(visit("v1", supervisorId = "s"), emptyList())
        dao.save(rep("s", "New Name", RepRole.Supervisor))
        assertEquals(listOf("v1"), db.visitDao().search("new name").allRows().map { it.visit.id })
        assertTrue(db.visitDao().search("old name").allRows().isEmpty())
    }
}
