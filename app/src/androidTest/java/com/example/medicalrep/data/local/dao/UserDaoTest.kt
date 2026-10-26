package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.domain.models.UserRole
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitType

/** CRUD, queries by role and supervisor, Arabic search, and save refreshing the search text of supervised visits. */
@RunWith(AndroidJUnit4::class)
class UserDaoTest : DaoTestBase() {
    private val dao get() = db.userDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(user("u1", "Ali"))
        assertEquals("Ali", dao.getById("u1")?.displayName)
        dao.update(user("u1", "Omar"))
        assertEquals("Omar", dao.getById("u1")?.displayName)
        assertEquals(1, dao.deleteById("u1"))
        assertNull(dao.getById("u1"))
    }

    @Test fun getAllAndClear() = runBlocking {
        dao.insertAll(listOf(user("a", "B"), user("b", "A")))
        assertEquals(listOf("b", "a"), dao.getAll().first().map { it.id })
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test fun byRoleAndRepsOfSupervisor() = runBlocking {
        dao.insertAll(listOf(user("s", role = UserRole.Supervisor), user("r1", supervisorId = null)))
        dao.insert(user("r2", supervisorId = "s"))
        assertEquals(listOf("s"), dao.getByRole(UserRole.Supervisor).first().map { it.id })
        assertEquals(listOf("r2"), dao.getRepsOf("s").first().map { it.id })
        assertTrue(dao.getRepsOf("none").first().isEmpty())
    }

    @Test fun searchPartialCaseAndArabic() = runBlocking {
        dao.insertAll(listOf(user("1", "Ahmed Ali"), user("2", "أحمد")))
        assertEquals(listOf("1"), dao.search("ahm").allRows().map { it.id })
        assertEquals(listOf("1", "2"), dao.search("").allRows().map { it.id })
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun searchByRoleOnlyReturnsThatRole() = runBlocking {
        dao.insertAll(listOf(user("s", "Sam", UserRole.Supervisor), user("r", "Sam", UserRole.Rep)))
        assertEquals(listOf("s"), dao.searchByRole(UserRole.Supervisor, "sam").allRows().map { it.id })
    }

    @Test fun deletingSupervisorClearsRepsSupervisorId() = runBlocking {
        dao.insert(user("s", role = UserRole.Supervisor))
        dao.insert(user("r", supervisorId = "s"))
        dao.deleteById("s")
        assertNull(dao.getById("r")?.supervisorId)
    }

    @Test fun saveRefreshesSearchTextOfSupervisedVisits() = runBlocking {
        dao.save(user("s", "Old Name", UserRole.Supervisor))
        db.visitDao().save(visit("v1", supervisorId = "s"), emptyList())
        dao.save(user("s", "New Name", UserRole.Supervisor))
        assertEquals(listOf("v1"), db.visitDao().search("new name").allRows().map { it.visit.id })
        assertTrue(db.visitDao().search("old name").allRows().isEmpty())
    }
}
