package com.example.medicalrep.ui.screens.planvisit.utils

import com.example.medicalrep.ui.models.VisitUi
import org.junit.Assert.assertEquals
import org.junit.Test

/** The account row shows "city - address", skipping whatever is blank. */
class AccountAddressTest {

    @Test
    fun `city and address are joined`() {
        assertEquals("Shebin El-Kom - Stadium St", VisitUi(city = "Shebin El-Kom", address = "Stadium St").accountAddressLine())
    }

    @Test
    fun `blank parts are skipped`() {
        assertEquals("Shebin El-Kom", VisitUi(city = "Shebin El-Kom", address = " ").accountAddressLine())
        assertEquals("Stadium St", VisitUi(city = "", address = "Stadium St").accountAddressLine())
        assertEquals("", VisitUi().accountAddressLine())
    }
}
