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
class UserRepoTest : RepoTestBase() {
    private val repo get() = UserRepoImpl(db.userDao())

    @Test fun saveGetDelete() = runTest {
        repo.save(User("u1", "Ali", "Rep"))
        assertEquals("Ali", repo.get("u1")?.displayName)
        repo.delete("u1")
        assertNull(repo.get("u1"))
    }

    @Test fun byRoleAndRepsOf() = runTest {
        repo.save(User("s", "Boss", "Sup", role = UserRole.Supervisor))
        repo.save(User("r", "Rep", "Rep", supervisorId = "s"))
        assertEquals(listOf("s"), repo.byRole(UserRole.Supervisor).first().map { it.id })
        assertEquals(listOf("r"), repo.repsOf("s").first().map { it.id })
    }

    @Test fun searchArabicForms() = runTest {
        repo.save(User("1", "أحمد", "Rep"))
        repo.save(User("2", "Sara", "Rep"))
        assertEquals(listOf("1"), repo.users("احمد").asSnapshot().map { it.id })
        assertEquals(2, repo.users("").asSnapshot().size)
    }
}
