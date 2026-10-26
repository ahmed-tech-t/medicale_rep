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

/** Round trip with lines and totals, paging, search, upload rules. */
@RunWith(AndroidJUnit4::class)
class OrderRepoTest : RepoTestBase() {
    private val repo get() = OrderRepoImpl(db.orderDao())

    private fun order(id: String, accountId: String? = null, createdAtMillis: Long = 1_700_000_000_000L) = Order(
        id = id, accountId = accountId, createdAtMillis = createdAtMillis,
        items = listOf(OrderItem("p1", "Panadol", 50, 10, 2), OrderItem("p2", "Brufen", 20, 0, 1)),
    )

    @Test fun saveThenGetWithAccountNameAndTotal() = runTest {
        AccountRepoImpl(db.accountDao()).save(Account("a1", name = "Clinic"))
        assertTrue(repo.save(order("o1", "a1")))
        val saved = repo.get("o1")!!
        assertEquals("Clinic", saved.accountName)
        assertEquals(2, saved.items.size)
        assertEquals(110, saved.total)
        assertNull(repo.get("none"))
    }

    @Test fun pagingAndSearch() = runTest {
        AccountRepoImpl(db.accountDao()).save(Account("a1", name = "صيدلية"))
        (1..60).forEach { repo.save(order("o$it", createdAtMillis = it * 1000L)) }
        repo.save(order("x", "a1"))
        assertTrue(repo.orders().asSnapshot().size in 20..40)
        assertEquals(listOf("x"), repo.orders("صيدليه").asSnapshot().map { it.id })
    }

    @Test fun uploadedOrderCannotBeChangedOrCancelled() = runTest {
        repo.save(order("o1"))
        assertEquals(listOf("o1"), repo.notSynced().map { it.id })
        repo.markSynced("o1", "srv")
        assertTrue(repo.notSynced().isEmpty())
        assertFalse(repo.save(order("o1").copy(items = emptyList())))
        assertFalse(repo.cancel("o1"))
        assertEquals(2, repo.get("o1")!!.items.size)
    }

    @Test fun cancelBeforeUpload() = runTest {
        repo.save(order("o1"))
        assertTrue(repo.cancel("o1"))
        assertFalse(repo.cancel("o1"))
    }

    @Test fun filteredByAccount() = runTest {
        AccountRepoImpl(db.accountDao()).save(Account("a1", name = "A"))
        repo.save(order("o1", "a1"))
        repo.save(order("o2"))
        assertEquals(listOf("o1"), repo.ordersFiltered("a1", null, null, 0, Long.MAX_VALUE).asSnapshot().map { it.id })
    }
}
