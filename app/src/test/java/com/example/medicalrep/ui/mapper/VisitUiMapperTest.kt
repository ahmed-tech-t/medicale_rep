package com.example.medicalrep.ui.mapper

import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.ui.models.DoctorUi
import com.example.medicalrep.domain.models.BasicModel
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
            id = "v1", city = BasicModelUi("c1", "Cairo"), region = BasicModelUi("r1", "Nasr"), account = BasicModelUi("t1", "عيادة"), placeName = "د. أحمد",
            specialty = BasicModelUi("sp1", "باطنة"),
            shift = Shift.Pm, type = VisitType.Planned, isDoubleVisit = true, supervisor = BasicModelUi("s1", "Boss"), noIssueFound = false,
            visitLatitude = 1.0, visitLongitude = 2.0, travelSeconds = 30,
            gifts = listOf(ProductUi("g1", "Pen", 2, ProductType.Gift, 8, "")),
            products = listOf(ProductUi("p1", "Drug", 1, ProductType.Sample, 4, "", 50, 10)),
        )
        val visit = ui.toDomain()
        assertEquals("v1", visit.id)
        assertEquals(BasicModel("c1", "Cairo"), visit.city)
        assertEquals(BasicModel("s1", "Boss"), visit.supervisor)
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
        val ui = VisitUi(id = "v", placeName = "x", doctors = listOf(DoctorUi("d1", "Dr. Ali", BasicModelUi("A", "A"))), gifts = listOf(ProductUi("g", "Pen", 1, ProductType.Gift, 5, "")))
        assertEquals(ui, ui.toDomain().toUi().copy(date = "", time = ""))
    }
}
