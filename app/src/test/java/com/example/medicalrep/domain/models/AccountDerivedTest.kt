package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** An account has no class or specialty of its own: they come from its doctors. */
class AccountDerivedTest {
    private fun doctor(id: String, classCategory: String? = null, specialty: String? = null) = Doctor(
        id,
        name = id,
        classCategory = classCategory?.let { BasicModel(it, it) },
        specialty = specialty?.let { BasicModel(it, it) },
    )

    private fun account(vararg doctors: Doctor) = Account("a", name = "x", doctors = doctors.toList())

    @Test
    fun `an account without doctors has no class and no specialty`() {
        assertNull(account().classCategory)
        assertNull(account().specialty)
    }

    @Test
    fun `a single doctor gives its class and specialty to the account`() {
        val clinic = account(doctor("d1", "B", "Cardio"))
        assertEquals("B", clinic.classCategory?.name)
        assertEquals("Cardio", clinic.specialty?.name)
    }

    @Test
    fun `the class is the best one among the doctors`() {
        assertEquals("A", account(doctor("d1", "C"), doctor("d2", "A"), doctor("d3", "B")).classCategory?.name)
    }

    @Test
    fun `doctors without a class are ignored`() {
        assertEquals("C", account(doctor("d1"), doctor("d2", "C")).classCategory?.name)
        assertNull(account(doctor("d1")).classCategory)
    }

    @Test
    fun `the specialty is kept only when the doctors share it`() {
        assertEquals("Cardio", account(doctor("d1", specialty = "Cardio"), doctor("d2", specialty = "Cardio")).specialty?.name)
        assertNull(account(doctor("d1", specialty = "Cardio"), doctor("d2", specialty = "Skin")).specialty)
    }
}
