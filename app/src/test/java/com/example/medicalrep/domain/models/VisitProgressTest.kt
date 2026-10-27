package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Test

/** The percent of planned visits that are done, rounded; 0 with nothing planned; never above 100. */
class VisitProgressTest {
    @Test
    fun `ten of twenty six is thirty eight percent`() = assertEquals(38, VisitProgress(completed = 10, planned = 26).percent)

    @Test
    fun `nothing planned is zero percent`() = assertEquals(0, VisitProgress(completed = 0, planned = 0).percent)

    @Test
    fun `everything done is a hundred percent`() = assertEquals(100, VisitProgress(completed = 4, planned = 4).percent)

    @Test
    fun `more done than planned stays at a hundred`() = assertEquals(100, VisitProgress(completed = 5, planned = 4).percent)
}
