package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.SyncInfo
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Account: every field maps; Synced means isSynced.
 * Custody: gift flag from kind; remaining includes pending.
 * Order: account name (empty when the account was deleted), date/time from millis (empty when 0), totals, lines.
 * TrackPoint: date and time formatted. SyncInfo: updated time formatted, empty when never updated.
 */
class NewUiMappersTest {

    @Test
    fun `account maps all fields and sync flag`() {
        val ui = Account(
            "a1", "b", "Clinic", BasicModel("Pharmacy", "Pharmacy"), BasicModel("Cairo", "Cairo"), BasicModel("Nasr", "Nasr"), "St 1", 1.5, 2.5, "s", SyncStatus.Synced,
            doctors = listOf(Doctor("d1", name = "Dr. Ali", classCategory = BasicModel("A", "A"), specialty = BasicModel("Cardio", "Cardio"))),
        ).toUi()
        assertEquals("a1", ui.id)
        assertEquals("Clinic", ui.name)
        assertEquals("Pharmacy", ui.accountType?.name)
        assertEquals("A", ui.classCategory?.name)
        assertEquals("Cardio", ui.specialty?.name)
        assertEquals("Cairo", ui.city?.name)
        assertEquals("Nasr", ui.district?.name)
        assertEquals("St 1", ui.address)
        assertEquals(1.5, ui.latitude, 0.0)
        assertEquals(2.5, ui.longitude, 0.0)
        assertTrue(ui.isSynced)
        assertFalse(Account("a2", name = "x").toUi().isSynced)
    }

    @Test
    fun `account type is read from the entity type text`() {
        assertEquals(AccountType.Hospital, Account("a", name = "x", accountType = BasicModel("مستشفى", "مستشفى")).toUi().type)
        assertEquals(null, Account("a", name = "x").toUi().type)
    }

    @Test
    fun `custody maps kind and remaining`() {
        val ui = Custody("c1", "Pen", ProductType.Gift, "Blue", received = 10, remaining = 5).toUi()
        assertTrue(ui.isGift)
        assertEquals(5, ui.remaining)
        assertFalse(Custody("c2", "Drug", ProductType.Sample).toUi().isGift)
    }

    @Test
    fun `order maps account lines totals and time`() {
        val millis = 1_792_000_000_000L
        val order = Order(
            id = "o1", number = 1001, accountName = "Clinic", createdAtMillis = millis, syncStatus = SyncStatus.Synced,
            items = listOf(OrderItem("p1", "Panadol", 50, 10, 2), OrderItem("p2", "Brufen", 20, 0, 1)),
        )
        val ui = order.toUi()
        assertEquals("Clinic", ui.accountName)
        assertEquals(formatDate(millis), ui.date)
        assertEquals(formatClockTime(millis), ui.time)
        assertEquals(1001, ui.number)
        assertEquals(110.0, ui.total, 0.0)
        assertTrue(ui.isSynced)
        assertEquals(listOf("Panadol", "Brufen"), ui.items.map { it.name })
        assertEquals(90.0, ui.items[0].lineTotal, 0.0)
    }

    @Test
    fun `order without account time or items gives empty text`() {
        val ui = Order(id = "o").toUi()
        assertEquals("", ui.accountName)
        assertEquals("", ui.date)
        assertEquals("", ui.time)
        assertEquals(0.0, ui.total, 0.0)
        assertTrue(ui.items.isEmpty())
        assertFalse(ui.isSynced)
    }

    @Test
    fun `track point formats date and time`() {
        val ui = TrackPoint(id = 4, repId = "r", latitude = 1.0, longitude = 2.0, timeMillis = 1_792_000_000_000L, syncStatus = SyncStatus.Synced).toUi()
        assertEquals(4L, ui.id)
        assertEquals(formatDate(1_792_000_000_000L), ui.date)
        assertEquals(formatClockTime(1_792_000_000_000L), ui.time)
        assertTrue(ui.isSynced)
    }

    @Test
    fun `sync info formats updated time and is empty when never updated`() {
        assertEquals("${formatDate(1_792_000_000_000L)} ${formatClockTime(1_792_000_000_000L)}", SyncInfo("products", 1_792_000_000_000L).toUi().lastUpdated)
        assertEquals("", SyncInfo("products", 0).toUi().lastUpdated)
        assertEquals("products", SyncInfo("products", 0).toUi().key)
    }
}
