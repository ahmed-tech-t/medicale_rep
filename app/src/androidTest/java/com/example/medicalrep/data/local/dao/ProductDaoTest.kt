package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.Product
import org.junit.Test
import org.junit.runner.RunWith

/** CRUD, paging and search for items for sale. */
@RunWith(AndroidJUnit4::class)
class ProductDaoTest : DaoTestBase() {
    private val dao get() = db.productDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(product("p1", "Panadol"))
        assertEquals("Panadol", dao.getById("p1")?.product?.name)
        dao.update(product("p1", "Brufen"))
        assertEquals("Brufen", dao.getById("p1")?.product?.name)
        assertEquals(1, dao.deleteById("p1"))
        assertNull(dao.getById("p1"))
    }

    @Test fun upsertReplacesInPlace() = runBlocking {
        dao.upsert(product("p1", "A"))
        dao.upsert(product("p1", "B"))
        assertEquals(listOf("B"), dao.getAll().first().map { it.product.name })
    }

    @Test fun pagingGivesTwentyPerPageOrderedByName() = runBlocking {
        dao.insertAll((1..60).map { product("p%02d".format(java.util.Locale.ROOT, it), "N%02d".format(java.util.Locale.ROOT, it)) })
        val page = dao.paged().firstPage()
        assertEquals(20, page.data.size)
        assertEquals("N01", page.data.first().product.name)
        assertNotNull(page.nextKey)
        assertNull(dao.paged().firstPage(100).nextKey)
    }

    @Test fun searchPartialCaseAndNoMatch() = runBlocking {
        dao.insertAll(listOf(product("1", "Panadol Extra"), product("2", "Brufen")))
        assertEquals(listOf("1"), dao.search("PANA".lowercase()).allRows().map { it.product.id })
        assertEquals(2, dao.search("").allRows().size)
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }

    @Test fun soldIsTheQuantityOfEveryOrderLineOfTheProduct() = runBlocking {
        dao.insert(Product(id = "p1", name = "A", received = 100).toEntity())
        dao.insert(Product(id = "p2", name = "B", received = 50).toEntity())
        db.orderDao().save(Order(id = "o1").toEntity(), listOf(OrderItemEntity("o1", "p1", "A", 10, 0, 30), OrderItemEntity("o1", "p2", "B", 5, 0, 5)))
        db.orderDao().save(Order(id = "o2").toEntity(), listOf(OrderItemEntity("o2", "p1", "A", 10, 0, 10)))
        assertEquals(40, dao.getById("p1")!!.sold)
        assertEquals(5, dao.getById("p2")!!.sold)
        assertEquals(60, dao.getById("p1")!!.toDomain().remaining)
        assertNull(dao.getById("none"))
    }

    @Test fun clearEmptiesTable() = runBlocking {
        dao.insert(product("1"))
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }
}
