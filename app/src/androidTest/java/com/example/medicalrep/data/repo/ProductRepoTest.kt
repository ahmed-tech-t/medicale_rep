package com.example.medicalrep.data.repo

import androidx.paging.testing.asSnapshot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.dao.rep
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Products: round trip, paging, search. Custody: type filter, search, what is left. */
@RunWith(AndroidJUnit4::class)
class ProductRepoTest : RepoTestBase() {
    private val repo get() = ProductRepoImpl(db.catalogDao())

    @Before fun addRep() = runTest { db.repDao().save(rep("r1")) }

    @Test fun productsRoundTripPagingAndSearch() = runTest {
        repo.saveProducts("r1", (1..60).map { Product(id = "p$it", name = "P${"%02d".format(java.util.Locale.ROOT, it)}", price = it) })
        repo.saveProducts("r1", listOf(Product(id = "x", name = "Panadol")))
        assertTrue(repo.products().asSnapshot().size in 20..40)
        assertEquals(listOf("x"), repo.products("PANA").asSnapshot().map { it.id })
        assertEquals("Panadol", repo.getProduct("x")?.name)
        repo.deleteProduct("x")
        assertNull(repo.getProduct("x"))
    }

    @Test fun aSavedProductHasAllOfItLeft() = runTest {
        repo.saveProducts("r1", listOf(Product(id = "p1", name = "A", received = 100)))
        assertEquals(100, repo.getProduct("p1")?.remaining)
    }

    @Test fun custodyTypeFilterAndSearch() = runTest {
        repo.saveCustody("r1", listOf(
            Custody("1", "Pen", ProductType.Gift), Custody("2", "Pen", ProductType.Sample), Custody("3", "قلم أزرق", ProductType.Gift),
        ))
        assertEquals(setOf("2"), repo.custody(ProductType.Sample).asSnapshot().map { it.id }.toSet())
        assertEquals(listOf("1"), repo.custody(ProductType.Gift, "pen").asSnapshot().map { it.id })
        assertEquals(listOf("3"), repo.custody(ProductType.Gift, "قلم ازرق").asSnapshot().map { it.id })
    }

    @Test fun custodyListGivesOneTypeWithWhatIsLeft() = runTest {
        repo.saveCustody("r1", listOf(Custody("1", "Pen", ProductType.Gift, received = 10, remaining = 6), Custody("2", "Drug", ProductType.Sample, received = 5)))
        assertEquals(listOf("1"), repo.custodyList(ProductType.Gift).first().map { it.id })
        assertEquals(6, repo.custodyList(ProductType.Gift).first().single().remaining)
        assertEquals(6, repo.getCustody("1")?.remaining)
        assertNull(repo.getCustody("none"))
    }
}
