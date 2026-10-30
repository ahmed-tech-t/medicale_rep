package com.example.medicalrep.ui.screens.planvisit.utils

import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.ProductType
import org.junit.Assert.assertEquals
import org.junit.Test

/** A custody row becomes a catalog item with quantity 1 and what is left in custody. */
class CatalogItemTest {
    @Test
    fun `custody becomes a catalog item`() {
        val item = Custody("c1", "Pen", ProductType.Gift, "Blue", received = 10, remaining = 5).toCatalogItem()
        assertEquals("c1", item.id)
        assertEquals("Pen", item.name)
        assertEquals(ProductType.Gift, item.kind)
        assertEquals(1, item.quantity)
        assertEquals(5, item.remaining)
    }
}
