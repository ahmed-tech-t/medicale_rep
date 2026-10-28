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

/** Round trip, role and supervisor queries, search. */
@RunWith(AndroidJUnit4::class)
class RepRepoTest : RepoTestBase() {
    private val repo get() = RepRepoImpl(db.repDao())

    @Test fun saveGetDelete() = runTest {
        repo.save(Rep("u1", "Ali"))
        assertEquals("Ali", repo.get("u1")?.displayName)
        repo.delete("u1")
        assertNull(repo.get("u1"))
    }

    @Test fun byRoleAndRepsOf() = runTest {
        repo.save(Rep("s", "Boss", role = RepRole.Supervisor))
        repo.save(Rep("r", "Rep", supervisorId = "s"))
        assertEquals(listOf("s"), repo.byRole(RepRole.Supervisor).first().map { it.id })
        assertEquals(listOf("r"), repo.repsOf("s").first().map { it.id })
    }

    @Test fun searchArabicForms() = runTest {
        repo.save(Rep("1", "أحمد"))
        repo.save(Rep("2", "Sara"))
        assertEquals(listOf("1"), repo.reps("احمد").asSnapshot().map { it.id })
        assertEquals(2, repo.reps("").asSnapshot().size)
    }
}
