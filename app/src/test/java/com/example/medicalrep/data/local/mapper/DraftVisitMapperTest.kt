package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.DraftVisit
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 1. A draft with every form field survives entity -> domain unchanged (step, type, texts, shift, double visit,
 *    coordinates, gift and product lines).
 * 2. The voice note path is written into the draft and the file bytes are read back through readVoice.
 * 3. No voice note gives null path and no bytes. A path whose file is gone gives no bytes.
 * 4. Unknown or missing json fields and unknown enum names fall back to defaults.
 */
class DraftVisitMapperTest {

    private val visit = Visit(
        id = "planned-9", type = VisitType.Planned, city = BasicModel("c1", "Cairo"), region = BasicModel("r1", "Nasr"), address = "St 1", account = BasicModel("t1", "عيادة"),
        placeName = "د. أحمد", specialty = BasicModel("sp1", "باطنة"), classCategory = BasicModel("classA", "A"), supervisor = BasicModel("s1", "Boss"), shift = Shift.Pm,
        visitMode = VisitMode.Double, noIssueFound = false, imageNotes = listOf("content://img/1"),
        visitLatitude = 1.5, visitLongitude = 2.5, accountLatitude = 3.5, accountLongitude = 4.5, travelSeconds = 90,
        gifts = listOf(Product("g1", "Pen", 2, ProductType.Gift, "Blue", 8, 0, 0)),
        products = listOf(Product("p1", "Drug", 1, ProductType.Product, "", 4, 50, 10)),
    )

    @Test
    fun `every form field survives a round trip`() {
        val back = DraftVisit(step = 1, visit = visit).toEntity(voiceNotePath = null).toDomain { null }
        assertEquals(1, back.step)
        assertEquals(visit, back.visit)
    }

    @Test
    fun `voice note path is stored and its bytes are read back`() {
        val entity = DraftVisit(0, visit.copy(voiceNote = byteArrayOf(1, 2))).toEntity(voiceNotePath = "/files/v.m4a")
        assertEquals("/files/v.m4a", entity.voiceNotePath())
        val back = entity.toDomain { path -> if (path == "/files/v.m4a") byteArrayOf(1, 2) else null }
        assertArrayEquals(byteArrayOf(1, 2), back.visit.voiceNote)
        assertEquals("/files/v.m4a", back.visit.voiceNotePath)
    }

    @Test
    fun `no voice note or a missing file gives no bytes`() {
        assertNull(DraftVisit(0, visit).toEntity(null).voiceNotePath())
        assertNull(DraftVisit(0, visit).toEntity(null).toDomain { null }.visit.voiceNote)
        assertNull(DraftVisit(0, visit).toEntity("/gone").toDomain { null }.visit.voiceNote)
    }

    @Test
    fun `unknown fields and enum names fall back to defaults`() {
        val entity = DraftVisit(0, visit).toEntity(null).copy(json = "{\"type\":\"Weird\",\"shift\":\"Night\",\"extra\":1}")
        val back = entity.toDomain { null }.visit
        assertEquals(VisitType.Unplanned, back.type)
        assertEquals(Shift.Am, back.shift)
        assertEquals(VisitMode.Single, back.visitMode)
        assertEquals(emptyList<Product>(), back.gifts)
    }
}
