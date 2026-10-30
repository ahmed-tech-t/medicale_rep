package com.example.medicalrep.ui.mapper

import com.example.medicalrep.ui.models.BasicModelUi
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.ui.models.VisitUi
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

    @Test
    fun `an ended visit becomes a completed visit that is not uploaded yet`() {
        val form = VisitUi(
            id = "v1", accountId = "a1", type = VisitType.Planned, shift = Shift.Pm, isDoubleVisit = true, supervisor = BasicModelUi("s1", "Boss"),
            noIssueFound = false, voiceNote = byteArrayOf(1), imageNotes = listOf(android.net.FakeUri()),
        )
        val visit = form.toCompletedVisit(stored = null, repId = "r1", supervisorId = "s1", startedAtMillis = 1_000L, completedAtMillis = 276_000L)
        assertEquals("v1", visit.id)
        assertEquals("a1", visit.accountId)
        assertEquals("r1", visit.repId)
        assertEquals("s1", visit.supervisorId)
        assertEquals(VisitType.Planned, visit.type)
        assertEquals(VisitStatus.Completed, visit.status)
        assertEquals(SyncStatus.NotSynced, visit.syncStatus)
        assertEquals(1_000L, visit.dateTimeMillis)
        assertEquals(276_000L, visit.completedAtMillis)
        assertEquals("04:35 د", visit.durationLabel)
        assertEquals(Shift.Pm, visit.shift)
        assertEquals(false, visit.noIssueFound)
    }

    @Test
    fun `a form without an account or rep stores them as missing`() {
        val visit = VisitUi(id = "v1").toCompletedVisit(stored = null, repId = null, supervisorId = null, startedAtMillis = 5L, completedAtMillis = 5L)
        assertEquals(null, visit.accountId)
        assertEquals(null, visit.repId)
        assertEquals(null, visit.supervisorId)
    }

    @Test
    fun `completing a stored visit keeps its server id and request times`() {
        val stored = Visit(id = "v1", backendId = "srv-9", requestedAtMillis = 5L, decidedAtMillis = 6L)
        val visit = VisitUi(id = "v1").toCompletedVisit(stored = stored, repId = null, supervisorId = null, startedAtMillis = 10L, completedAtMillis = 20L)
        assertEquals("srv-9", visit.backendId)
        assertEquals(5L, visit.requestedAtMillis)
        assertEquals(6L, visit.decidedAtMillis)
        assertEquals(VisitStatus.Completed, visit.status)
    }
}
