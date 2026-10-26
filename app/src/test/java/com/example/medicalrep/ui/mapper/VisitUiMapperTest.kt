package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.VisitType
import com.example.medicalrep.ui.models.ProductUi
import com.example.medicalrep.ui.models.VisitUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The visit form (VisitUi) maps to a domain Visit with all editable fields; blank id means no id. */
class VisitUiMapperTest {

    @Test
    fun `form fields reach the domain visit`() {
        val ui = VisitUi(
            id = "v1", city = "Cairo", region = "Nasr", account = "عيادة", placeName = "د. أحمد", specialty = "باطنة",
            shift = Shift.Pm, type = VisitType.Planned, isDoubleVisit = true, supervisorName = "Boss", noIssueFound = false,
            visitLatitude = 1.0, visitLongitude = 2.0, travelSeconds = 30,
            gifts = listOf(ProductUi("g1", "Pen", 2, ProductType.Gift, 8, "")),
            products = listOf(ProductUi("p1", "Drug", 1, ProductType.Product, 4, "", 50, 10)),
        )
        val visit = ui.toDomain()
        assertEquals("v1", visit.id)
        assertEquals("Cairo", visit.city)
        assertEquals("د. أحمد", visit.placeName)
        assertEquals(Shift.Pm, visit.shift)
        assertEquals(com.example.medicalrep.domain.models.VisitMode.Double, visit.visitMode)
        assertEquals(false, visit.noIssueFound)
        assertEquals(30, visit.travelSeconds)
        assertEquals(listOf(Product("g1", "Pen", 2, ProductType.Gift, "", 8, 0, 0)), visit.gifts)
        assertEquals(50, visit.products?.single()?.price)
    }

    @Test
    fun `blank id gives no id and ui round trip keeps the lists`() {
        assertNull(VisitUi().toDomain().id)
        val ui = VisitUi(id = "v", placeName = "x", gifts = listOf(ProductUi("g", "Pen", 1, ProductType.Gift, 5, "")))
        assertEquals(ui, ui.toDomain().toUi().copy(date = "", time = ""))
    }
}
