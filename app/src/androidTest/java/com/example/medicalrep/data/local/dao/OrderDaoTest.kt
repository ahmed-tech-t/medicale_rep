package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.SyncStatus

private fun order(
    id: String,
    accountId: String? = null,
    repId: String? = null,
    syncStatus: SyncStatus = SyncStatus.NotSynced,
    createdAtMillis: Long = 1_700_000_000_000L,
) = OrderEntity(id, null, repId, accountId, createdAtMillis, syncStatus)

private fun item(orderId: String, productId: String, quantity: Int = 1) =
    OrderItemEntity(orderId, productId, productId, 10, 0, quantity)

/** CRUD, paging, search through the account and rep, filters, lines, edit rules and upload flags. */
@RunWith(AndroidJUnit4::class)
class OrderDaoTest : DaoTestBase() {
    private val dao get() = db.orderDao()

    @Test fun saveGetUpdateDelete() = runBlocking {
        assertTrue(dao.save(order("o1"), listOf(item("o1", "p1", 2))))
        assertEquals(2, dao.getWithDetails("o1")!!.items.single().quantity)
        assertTrue(dao.save(order("o1"), listOf(item("o1", "p1", 5))))
        assertEquals(5, dao.getWithDetails("o1")!!.items.single().quantity)
        assertEquals(1, dao.deleteById("o1"))
        assertNull(dao.getById("o1"))
    }

    @Test fun aNewOrderGetsTheNextNumberAndAnEditKeepsIt() = runBlocking {
        dao.save(order("o1"), emptyList())
        dao.save(order("o2"), emptyList())
        assertEquals(listOf(1, 2), listOf("o1", "o2").map { dao.getById(it)!!.number })
        dao.save(order("o1"), listOf(item("o1", "p1")))
        assertEquals(1, dao.getById("o1")!!.number)
        dao.save(order("o3"), emptyList())
        assertEquals(3, dao.getById("o3")!!.number)
    }

    @Test fun aGivenNumberIsKeptAndTheNextOneFollowsIt() = runBlocking {
        dao.save(order("o1").copy(number = 1001), emptyList())
        dao.save(order("o2"), emptyList())
        assertEquals(1001, dao.getById("o1")!!.number)
        assertEquals(1002, dao.getById("o2")!!.number)
    }

    @Test fun sameProductTwiceIsOneLine() = runBlocking {
        dao.save(order("o1"), listOf(item("o1", "p1", 1), item("o1", "p1", 4)))
        assertEquals(listOf(4), dao.getWithDetails("o1")!!.items.map { it.quantity })
    }

    @Test fun pagingIsNewestFirstTwentyPerPage() = runBlocking {
        (1..60).forEach { dao.save(order("o%02d".format(java.util.Locale.ROOT, it), createdAtMillis = it * 1000L), emptyList()) }
        val page = dao.paged().firstPage()
        assertEquals(20, page.data.size)
        assertEquals("o60", page.data.first().order.id)
        assertNotNull(page.nextKey)
        assertNull(dao.paged().firstPage(100).nextKey)
    }

    @Test fun searchByAccountRepAndDate() = runBlocking {
        db.accountDao().save(account("a1", "صيدلية الإيمان", "Cairo"))
        db.repDao().save(rep("r1", "Mona"))
        dao.save(order("o1", accountId = "a1", repId = "r1"), emptyList())
        dao.save(order("o2"), emptyList())
        assertEquals(listOf("o1"), dao.search(normalizeSearch("صيدليه الايمان")).allRows().map { it.order.id })
        assertEquals(listOf("o1"), dao.search("mona").allRows().map { it.order.id })
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT).format(java.util.Date(1_700_000_000_000L))
        assertEquals(2, dao.search(date).allRows().size)
        assertEquals(2, dao.search("").allRows().size)
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun filtersAreOptional() = runBlocking {
        db.accountDao().save(account("a1"))
        db.repDao().save(rep("r1"))
        dao.save(order("o1", accountId = "a1", createdAtMillis = 100), emptyList())
        dao.save(order("o2", repId = "r1", syncStatus = SyncStatus.Synced, createdAtMillis = 200), emptyList())
        dao.markSynced("o2", "b")
        assertEquals(2, dao.pagedFiltered(null, null, null, 0, 1000).allRows().size)
        assertEquals(listOf("o1"), dao.pagedFiltered("a1", null, null, 0, 1000).allRows().map { it.order.id })
        assertEquals(listOf("o2"), dao.pagedFiltered(null, "r1", null, 0, 1000).allRows().map { it.order.id })
        assertEquals(listOf("o2"), dao.pagedFiltered(null, null, SyncStatus.Synced, 0, 1000).allRows().map { it.order.id })
        assertEquals(listOf("o2"), dao.pagedFiltered(null, null, null, 150, 1000).allRows().map { it.order.id })
    }

    @Test fun deletingTheOrderDeletesItsLines() = runBlocking {
        dao.save(order("o1"), listOf(item("o1", "p1")))
        dao.deleteById("o1")
        assertTrue(db.query("SELECT * FROM order_items", null).use { it.count == 0 })
    }

    @Test fun deletingAccountKeepsTheOrderWithNullAccount() = runBlocking {
        db.accountDao().save(account("a1"))
        dao.save(order("o1", accountId = "a1"), listOf(item("o1", "p1")))
        db.accountDao().deleteById("a1")
        val details = dao.getWithDetails("o1")!!
        assertNull(details.order.accountId)
        assertNull(details.account)
        assertEquals(1, details.items.size)
    }

    @Test fun replacingAProductKeepsOrderLines() = runBlocking {
        db.productDao().upsert(product("p1", "A"))
        dao.save(order("o1"), listOf(item("o1", "p1")))
        db.productDao().upsert(product("p1", "B"))
        db.productDao().deleteById("p1")
        assertEquals(1, dao.getWithDetails("o1")!!.items.size)
    }

    @Test fun notSyncedAndMarkSynced() = runBlocking {
        dao.save(order("o1"), emptyList())
        assertEquals(listOf("o1"), dao.getNotSynced().map { it.id })
        dao.markSynced("o1", "srv")
        assertTrue(dao.getNotSynced().isEmpty())
        assertEquals("srv", dao.getById("o1")?.backendId)
    }

    @Test fun uploadedOrderCannotBeEditedOrCancelled() = runBlocking {
        dao.save(order("o1"), listOf(item("o1", "p1", 1)))
        dao.markSynced("o1", "srv")
        assertFalse(dao.save(order("o1", syncStatus = SyncStatus.NotSynced), listOf(item("o1", "p1", 9))))
        assertEquals(1, dao.getWithDetails("o1")!!.items.single().quantity)
        assertEquals(0, dao.cancel("o1"))
        assertNotNull(dao.getById("o1"))
    }

    @Test fun notUploadedOrderCanBeCancelled() = runBlocking {
        dao.save(order("o1"), emptyList())
        assertEquals(1, dao.cancel("o1"))
        assertEquals(0, dao.cancel("none"))
    }

    @Test fun renamingAccountOrRepRefreshesSearchText() = runBlocking {
        db.accountDao().save(account("a1", "Old Clinic"))
        db.repDao().save(rep("r1", "Old Rep"))
        dao.save(order("o1", accountId = "a1", repId = "r1"), emptyList())
        db.accountDao().save(account("a1", "New Clinic"))
        db.repDao().save(rep("r1", "New Rep"))
        assertEquals(1, dao.search("new clinic").allRows().size)
        assertEquals(1, dao.search("new rep").allRows().size)
        assertTrue(dao.search("old clinic").allRows().isEmpty())
    }

    @Test fun clearEmptiesTable() = runBlocking {
        dao.save(order("o1"), emptyList())
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }
}
