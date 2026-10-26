package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 1. The visit's own fields reach VisitUi: backend id, both coordinate pairs, travel time, no-issue flag.
 * 2. Missing fields get their UI defaults (empty text, zero, no issue found).
 * 3. The date and time text are formatted from dateTimeMillis when the visit has no text of its own,
 *    and a text the visit does carry wins.
 */
class VisitMapperTest {

    @Test
    fun `new visit fields reach the ui model`() {
        val ui = Visit(
            id = "v1",
            backendId = "432423",
            visitLatitude = 30.1,
            visitLongitude = 31.2,
            accountLatitude = 30.3,
            accountLongitude = 31.4,
            travelSeconds = 900,
            noIssueFound = false,
            type = VisitType.Planned,
        ).toUi()
        assertEquals("432423", ui.backendId)
        assertEquals(30.1, ui.visitLatitude, 0.0)
        assertEquals(31.2, ui.visitLongitude, 0.0)
        assertEquals(30.3, ui.accountLatitude, 0.0)
        assertEquals(31.4, ui.accountLongitude, 0.0)
        assertEquals(900, ui.travelSeconds)
        assertEquals(false, ui.noIssueFound)
    }

    @Test
    fun `missing fields get ui defaults`() {
        val ui = Visit().toUi()
        assertEquals("", ui.backendId)
        assertEquals(0.0, ui.visitLatitude, 0.0)
        assertEquals(0, ui.travelSeconds)
        assertTrue(ui.noIssueFound)
    }

    @Test
    fun `date and time are formatted from millis unless the visit has text`() {
        val millis = 1_792_000_000_000L
        val formatted = Visit(dateTimeMillis = millis).toUi()
        assertTrue(formatted.date.isNotBlank())
        assertTrue(formatted.time.isNotBlank())

        val explicit = Visit(date = "17/10/2026", time = "10:30", dateTimeMillis = millis).toUi()
        assertEquals("17/10/2026", explicit.date)
        assertEquals("10:30", explicit.time)

        val none = Visit().toUi()
        assertEquals("", none.date)
        assertEquals("", none.time)
    }
}
