package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.relation.CustodyWithPending
import com.example.medicalrep.data.local.relation.ProductWithSold
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.Product
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncInfo
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.TrackPoint
import com.example.medicalrep.domain.models.Rep
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.Visit
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Given a domain model / entity, When it is mapped there and back, Then nothing is lost.
 * Also: searchText is built and normalized; custody remaining counts pending; a visit takes its
 * account/supervisor text from the relations and its lines from custody + quantity; missing visit
 * fields get defaults; a visit without an id is rejected.
 */
class EntityMappersTest {

    @Test
    fun `rep round trip keeps role and supervisor and builds search text from name and code`() {
        val rep = Rep("u1", "أحمد", employeeCode = "E1", deviceId = "d1", role = RepRole.Supervisor, supervisorId = "boss")
        val entity = rep.toEntity()
        assertEquals(rep, entity.toDomain())
        assertEquals("احمد e1", entity.searchText)
    }

    @Test
    fun `account round trip`() {
        val account = Account("a1", "b1", "صيدلية", "Pharmacy", "A", "Cardio", "Cairo", "Nasr", "St 1", 30.1, 31.2, "s1", SyncStatus.Synced, "p.png", 99L)
        val entity = account.toEntity()
        assertEquals(account, entity.toDomain())
        assertTrue(entity.searchText.contains("صيدليه"))
        assertTrue(entity.searchText.contains("cairo"))
    }

    @Test
    fun `account entity stores its type read from the entity type text`() {
        assertEquals(com.example.medicalrep.domain.models.AccountType.Pharmacy, Account("a", name = "x", entityType = "صيدلية").toEntity().accountType)
        assertEquals(null, Account("a", name = "x", entityType = "").toEntity().accountType)
    }

    @Test
    fun `what orders sold is taken off what the rep received, and never goes below zero`() {
        val entity = Product(id = "p1", name = "Panadol", received = 100).toEntity()
        assertEquals(60, ProductWithSold(entity, sold = 40).toDomain().remaining)
        assertEquals(100, ProductWithSold(entity, sold = 40).toDomain().received)
        assertEquals(0, ProductWithSold(entity, sold = 130).toDomain().remaining)
    }

    @Test
    fun `product round trip and id is required`() {
        val product = Product(id = "p1", name = "Panadol", description = "Pain", price = 50, discountPercent = 10, received = 100, remaining = 100)
        val entity = product.toEntity()
        assertEquals(100, entity.quantity)
        assertEquals(product, ProductWithSold(entity, sold = 0).toDomain())
        assertEquals("panadol pain", entity.searchText)
        assertTrue(runCatching { Product(name = "x").toEntity() }.isFailure)
    }

    @Test
    fun `custody round trip and remaining counts pending`() {
        val custody = Custody("c1", "Pen", ProductType.Gift, "Blue", received = 10, dispatched = 3)
        assertEquals(custody, custody.toEntity().toDomain())
        assertEquals(7, custody.remaining)
        val withPending = CustodyWithPending(custody.toEntity(), pending = 2).toDomain()
        assertEquals(2, withPending.pending)
        assertEquals(5, withPending.remaining)
    }

    @Test
    fun `track point and sync info round trip`() {
        val point = TrackPoint(5L, "b", "r1", "v1", 1.0, 2.0, 123L, SyncStatus.Synced)
        assertEquals(point, point.toEntity().toDomain())
        val info = SyncInfo("products", 77L)
        assertEquals(info, info.toEntity().toDomain())
    }

    @Test
    fun `visit entity round trip keeps its own fields`() {
        val visit = Visit(
            id = "v1", backendId = "b1", repId = "r1", supervisorId = "s1", accountId = "a1", dateTimeMillis = 1000L,
            shift = Shift.Pm, type = VisitType.Planning, visitMode = VisitMode.Double, visitLatitude = 1.0, visitLongitude = 2.0,
            travelSeconds = 60, noIssueFound = false, imageNote = "img", voiceNotePath = "voice.m4a", syncStatus = SyncStatus.Synced,
            durationLabel = "10 min", status = VisitStatus.Rejected, requestedAtMillis = 1L, decidedAtMillis = 2L,
        )
        val back = VisitWithDetails(visit.toEntity(), null, null, emptyList(), emptyList()).toDomain()
        assertEquals(visit.copy(gifts = emptyList(), products = emptyList()), back)
    }

    @Test
    fun `visit takes account supervisor and lines from the relations`() {
        val account = Account("a1", name = "Clinic", entityType = "Pharmacy", city = "Cairo", district = "Nasr", address = "St 1", specialty = "Cardio", classCategory = "A", latitude = 3.0, longitude = 4.0).toEntity()
        val supervisor = Rep("s1", "Boss").toEntity()
        val gift = Custody("c1", "Pen", ProductType.Gift, received = 10, dispatched = 2).toEntity()
        val sample = Custody("c2", "Drug", ProductType.Product, received = 5).toEntity()
        val details = VisitWithDetails(
            visit = Visit(id = "v1", accountId = "a1", supervisorId = "s1", status = VisitStatus.Scheduled).toEntity(),
            account = account,
            supervisor = supervisor,
            custody = listOf(gift, sample),
            lines = listOf(VisitCustodyEntity("v1", "c1", 2), VisitCustodyEntity("v1", "c2", 1)),
        )
        val visit = details.toDomain()
        assertEquals("Pharmacy", visit.account)
        assertEquals("Clinic", visit.placeName)
        assertEquals("Cairo", visit.city)
        assertEquals("Nasr", visit.region)
        assertEquals("St 1", visit.address)
        assertEquals(3.0, visit.accountLatitude, 0.0)
        assertEquals("Boss", visit.supervisorName)
        assertEquals(listOf("c1"), visit.gifts?.map { it.id })
        assertEquals(2, visit.gifts?.single()?.quantity)
        assertEquals(8, visit.gifts?.single()?.remaining)
        assertEquals(listOf("c2"), visit.products?.map { it.id })
        assertEquals(1, visit.products?.single()?.quantity)
    }

    @Test
    fun `visit lines come from gifts and products`() {
        val visit = Visit(id = "v1", gifts = listOf(Product(id = "c1", quantity = 2)), products = listOf(Product(id = "c2", quantity = 1)))
        assertEquals(listOf(VisitCustodyEntity("v1", "c1", 2), VisitCustodyEntity("v1", "c2", 1)), visit.toLines())
        assertTrue(Visit(id = "v1").toLines().isEmpty())
    }

    @Test
    fun `missing visit fields get defaults and zero time reads back as null`() {
        val entity = Visit(id = "v1", status = VisitStatus.Scheduled).toEntity()
        assertEquals(Shift.Am, entity.shift)
        assertEquals(VisitType.Unplanned, entity.type)
        assertEquals(VisitMode.Single, entity.visitMode)
        assertEquals(SyncStatus.NotSynced, entity.syncStatus)
        assertEquals(0L, entity.dateTimeMillis)
        assertNull(VisitWithDetails(entity, null, null, emptyList(), emptyList()).toDomain().dateTimeMillis)
    }

    @Test
    fun `visit without id is rejected`() {
        assertTrue(runCatching { Visit(status = VisitStatus.Scheduled).toEntity() }.isFailure)
    }

    @Test
    fun `visit without a status is rejected instead of silently becoming scheduled`() {
        val error = runCatching { Visit(id = "v1").toEntity() }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertTrue(error!!.message!!.contains("status"))
    }

    @Test
    fun `every status is stored as it is`() {
        VisitStatus.entries.forEach { assertEquals(it, Visit(id = "v1", status = it).toEntity().status) }
    }
}
