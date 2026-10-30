package com.example.medicalrep.domain.models

import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.data.local.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** An account is open in the morning, the evening or both; unknown text reads as both. */
class WorkingTimeTest {

    @Test
    fun `each working time offers its shifts and only one gives a fixed shift`() {
        assertEquals(listOf(Shift.Am), WorkingTime.Am.shifts)
        assertEquals(listOf(Shift.Pm), WorkingTime.Pm.shifts)
        assertEquals(listOf(Shift.Am, Shift.Pm), WorkingTime.Both.shifts)
        assertEquals(Shift.Am, WorkingTime.Am.onlyShift)
        assertEquals(Shift.Pm, WorkingTime.Pm.onlyShift)
        assertNull(WorkingTime.Both.onlyShift)
    }

    @Test
    fun `the stored key reads back and anything unknown is both`() {
        WorkingTime.entries.forEach { assertEquals(it, WorkingTime.fromKey(it.key)) }
        assertEquals(WorkingTime.Both, WorkingTime.fromKey(null))
        assertEquals(WorkingTime.Both, WorkingTime.fromKey(""))
        assertEquals(WorkingTime.Both, WorkingTime.fromKey("Night"))
    }

    @Test
    fun `an account keeps its working time through Room and a new one defaults to both`() {
        assertEquals(WorkingTime.Pm, Account("a", name = "x", workingTime = WorkingTime.Pm).toEntity().toDomain().workingTime)
        assertEquals(WorkingTime.Both, Account("a", name = "x").toEntity().toDomain().workingTime)
    }
}
