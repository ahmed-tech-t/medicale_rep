package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.mapper.toCustody
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.data.local.mapper.toProduct
import com.example.medicalrep.data.local.search.normalizeSearch
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Catalog items of three types with the rep's stock.
 *
 * Given items of every type, then each list shows only its type, and only products have a price.
 * Given an item without an inventory row, then it reads as 0 held.
 * Given stock 10, when a not uploaded visit gives 3, then 7 are left; saved again with 5, then 5 are left.
 * Given a visit is uploaded already, when it is saved, then the stock is not changed again.
 * Given more is given than is held, then the stock stops at 0.
 * Given a visit is deleted, then the stock is not given back.
 * Given a catalog row is replaced by a download, then its stock and the visit lines stay.
 * Given a product sold by orders, then what is left of it is held minus sold.
 */
@RunWith(AndroidJUnit4::class)
class CatalogDaoTest : DaoTestBase() {
    private val dao get() = db.catalogDao()

    @Test fun insertGetUpdateDelete() = runBlocking {
        dao.insert(custody("c1", "Pen"))
        assertEquals("Pen", dao.getById("c1")?.item?.name)
        dao.update(custody("c1", "Cap"))
        assertEquals("Cap", dao.getById("c1")?.item?.name)
        assertEquals(1, dao.deleteById("c1"))
        assertNull(dao.getById("c1"))
    }

    @Test fun eachTypeHasItsOwnListAndOnlyProductsHaveAPrice() = runBlocking {
        dao.insertAll(listOf(product("p", "Panadol"), custody("s", "Sample", ProductType.Sample), custody("g", "Gift", ProductType.Gift)))
        assertEquals(listOf("s"), dao.ofType(ProductType.Sample).first().map { it.item.id })
        assertEquals(listOf("g"), dao.ofType(ProductType.Gift).first().map { it.item.id })
        assertEquals(listOf("p"), dao.search(ProductType.Product, "").allRows().map { it.item.id })
        assertNull(dao.getById("g")!!.item.price)
    }

    @Test fun anItemWithoutInventoryReadsAsZero() = runBlocking {
        dao.insert(custody("c1"))
        val row = dao.getById("c1")!!
        assertEquals(0, row.original)
        assertEquals(0, row.stock)
    }

    @Test fun pagingAndSearchByTypeAndText() = runBlocking {
        dao.insertAll((1..30).map { custody("g%02d".format(java.util.Locale.ROOT, it), "G%02d".format(java.util.Locale.ROOT, it), ProductType.Gift) })
        dao.insertAll(listOf(custody("1", "قلم أزرق", ProductType.Sample), custody("2", "Cap", ProductType.Sample)))
        assertEquals(20, dao.search(ProductType.Gift, "").firstPage().data.size)
        assertEquals(30, dao.search(ProductType.Gift, "").allRows().size)
        assertEquals(listOf("1"), dao.search(ProductType.Sample, normalizeSearch("قلم ازرق")).allRows().map { it.item.id })
        assertEquals(listOf("2"), dao.search(ProductType.Sample, "ca").allRows().map { it.item.id })
        assertTrue(dao.search(ProductType.Gift, "cap").allRows().isEmpty())
    }

    @Test fun inventoryIsSavedWithTheItems() = runBlocking {
        db.repDao().save(rep("r1"))
        dao.saveItems(listOf(custody("c1")), listOf(inventory("r1", "c1", original = 10, current = 6)))
        val row = dao.getById("c1")!!
        assertEquals(10, row.original)
        assertEquals(6, row.toCustody().remaining)
    }

    @Test fun aNotUploadedVisitTakesWhatItGaveFromTheStock() = runBlocking {
        db.addStock("c1", original = 10)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.NotSynced), listOf(VisitItemEntity("v1", "c1", 3)))
        assertEquals(7, dao.getById("c1")!!.stock)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.NotSynced), listOf(VisitItemEntity("v1", "c1", 5)))
        assertEquals(5, dao.getById("c1")!!.stock)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.NotSynced), emptyList())
        assertEquals(10, dao.getById("c1")!!.stock)
    }

    @Test fun anUploadedVisitDoesNotChangeTheStock() = runBlocking {
        db.addStock("c1", original = 10)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.Synced), listOf(VisitItemEntity("v1", "c1", 4)))
        assertEquals(10, dao.getById("c1")!!.stock)
    }

    @Test fun theStockNeverGoesBelowZero() = runBlocking {
        db.addStock("c1", original = 2)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.NotSynced), listOf(VisitItemEntity("v1", "c1", 5)))
        assertEquals(0, dao.getById("c1")!!.stock)
    }

    @Test fun deletingAVisitDoesNotGiveTheStockBack() = runBlocking {
        db.addStock("c1", original = 10)
        db.visitDao().save(visit("v1", syncStatus = SyncStatus.NotSynced), listOf(VisitItemEntity("v1", "c1", 3)))
        db.visitDao().deleteById("v1")
        assertEquals(7, dao.getById("c1")!!.stock)
    }

    @Test fun replacingACatalogRowKeepsItsStock() = runBlocking {
        db.addStock("c1", original = 10)
        dao.upsert(custody("c1", "Renamed"))
        assertEquals(10, dao.getById("c1")!!.stock)
    }

    @Test fun whatIsLeftOfAProductIsHeldMinusSold() = runBlocking {
        db.addStock("p1", original = 100, type = ProductType.Product)
        db.orderDao().save(Order(id = "o1").toEntity(), listOf(OrderItemEntity("o1", "p1", "A", 10, 0, 30)))
        db.orderDao().save(Order(id = "o2").toEntity(), listOf(OrderItemEntity("o2", "p1", "A", 10, 0, 10)))
        val row = dao.getById("p1")!!
        assertEquals(40, row.sold)
        assertEquals(60, row.toProduct().remaining)
    }

    @Test fun clearEmptiesTable() = runBlocking {
        dao.insert(product("1"))
        dao.clear()
        assertTrue(dao.ofType(ProductType.Product).first().isEmpty())
    }
}
