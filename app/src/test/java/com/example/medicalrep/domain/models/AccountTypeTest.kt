package com.example.medicalrep.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Arabic and English names map to a type; blank or unknown text gives null. */
class AccountTypeTest {

    @Test
    fun `arabic names map to types`() {
        assertEquals(AccountType.Clinic, AccountType.fromEntityType("عيادة"))
        assertEquals(AccountType.Clinic, AccountType.fromEntityType("عيادة خاصة"))
        assertEquals(AccountType.Hospital, AccountType.fromEntityType("مستشفى"))
        assertEquals(AccountType.Pharmacy, AccountType.fromEntityType("صيدلية"))
        assertEquals(AccountType.MedicalCenter, AccountType.fromEntityType("مركز طبي"))
    }

    @Test
    fun `english names map to types ignoring case`() {
        assertEquals(AccountType.Clinic, AccountType.fromEntityType("Clinic"))
        assertEquals(AccountType.Hospital, AccountType.fromEntityType("HOSPITAL"))
        assertEquals(AccountType.Pharmacy, AccountType.fromEntityType("pharmacy"))
        assertEquals(AccountType.MedicalCenter, AccountType.fromEntityType("Medical Center"))
    }

    @Test
    fun `blank or unknown text gives null`() {
        assertNull(AccountType.fromEntityType(""))
        assertNull(AccountType.fromEntityType("   "))
        assertNull(AccountType.fromEntityType("laboratory"))
    }
}
