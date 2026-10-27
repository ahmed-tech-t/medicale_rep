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

/** Products: round trip, paging, search. Custody: kind filter, search, remaining with not uploaded visits. */
@RunWith(AndroidJUnit4::class)
class ProductRepoTest : RepoTestBase() {
    private val repo get() = ProductRepoImpl(db.productDao(), db.custodyDao())

    @Test fun productsRoundTripPagingAndSearch() = runTest {
        repo.saveProducts((1..60).map { Product(id = "p$it", name = "P${"%02d".format(java.util.Locale.ROOT, it)}", price = it) })
        repo.saveProducts(listOf(Product(id = "x", name = "Panadol")))
        assertTrue(repo.products().asSnapshot().size in 20..40)
        assertEquals(listOf("x"), repo.products("PANA").asSnapshot().map { it.id })
        assertEquals("Panadol", repo.getProduct("x")?.name)
        repo.deleteProduct("x")
        assertNull(repo.getProduct("x"))
    }

    @Test fun custodyKindFilterAndSearch() = runTest {
        repo.saveCustody(listOf(
            Custody("1", "Pen", ProductType.Gift), Custody("2", "Pen", ProductType.Product), Custody("3", "قلم أزرق", ProductType.Gift),
        ))
        assertEquals(3, repo.custody().asSnapshot().size)
        assertEquals(setOf("2"), repo.custody(kind = ProductType.Product).asSnapshot().map { it.id }.toSet())
        assertEquals(listOf("1"), repo.custody("pen", ProductType.Gift).asSnapshot().map { it.id })
        assertEquals(listOf("3"), repo.custody("قلم ازرق").asSnapshot().map { it.id })
    }

    @Test fun custodyListGivesOneKindWithRemaining() = runTest {
        repo.saveCustody(listOf(Custody("1", "Pen", ProductType.Gift, received = 10, dispatched = 4), Custody("2", "Drug", ProductType.Product, received = 5)))
        assertEquals(listOf("1"), repo.custodyList(ProductType.Gift).first().map { it.id })
        assertEquals(6, repo.custodyList(ProductType.Gift).first().single().remaining)
    }

    @Test fun custodyRemainingSubtractsVisitsNotUploadedYet() = runTest {
        repo.saveCustody(listOf(Custody("c1", "Pen", ProductType.Gift, received = 10, dispatched = 2)))
        val visits = VisitRepoImpl(db.visitDao(), media)
        visits.save(Visit(id = "v1", status = VisitStatus.Scheduled, syncStatus = SyncStatus.NotSynced, gifts = listOf(Product(id = "c1", quantity = 3))))
        visits.save(Visit(id = "v2", status = VisitStatus.Scheduled, syncStatus = SyncStatus.Synced, gifts = listOf(Product(id = "c1", quantity = 4))))
        assertEquals(5, repo.getCustody("c1")?.remaining)
        assertEquals(5, repo.custody().asSnapshot().single().remaining)
        assertNull(repo.getCustody("none"))
    }
}
