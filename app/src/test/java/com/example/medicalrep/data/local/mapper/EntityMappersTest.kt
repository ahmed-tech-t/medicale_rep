package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.UserInventoryEntity
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.relation.CatalogItemWithStock
import com.example.medicalrep.data.local.relation.VisitWithDetails
import com.example.medicalrep.domain.models.visitStatusOf
import com.example.medicalrep.domain.models.Doctor
import com.example.medicalrep.domain.models.BasicModel
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
        val doctor = Doctor("d1", name = "Dr. Ali", classCategory = BasicModel("A", "A"), specialty = BasicModel("Cardio", "Cardio"))
        val account = Account(
            "a1", "b1", "صيدلية", BasicModel("Pharmacy", "Pharmacy"), BasicModel("Cairo", "Cairo"), BasicModel("Nasr", "Nasr"),
            "St 1", 30.1, 31.2, "s1", SyncStatus.Synced, "p.png", 99L, doctors = listOf(doctor),
        )
        val entity = account.toEntity()
        assertEquals(account, entity.withPlaceOf(account).toDomain())
        assertTrue(entity.searchText.contains("صيدليه"))
        assertTrue(entity.searchText.contains("cairo"))
    }

    @Test
    fun `account entity stores its type read from the entity type text`() {
        assertEquals(com.example.medicalrep.domain.models.AccountType.Pharmacy, Account("a", name = "x", accountType = BasicModel("صيدلية", "صيدلية")).toEntity().accountType)
        assertEquals(null, Account("a", name = "x", accountType = null).toEntity().accountType)
    }

    @Test
    fun `what orders sold is taken off what the rep received, and never goes below zero`() {
        val item = Product(id = "p1", name = "Panadol", received = 100).toItem()
        assertEquals(60, CatalogItemWithStock(item, original = 100, stock = 100, sold = 40).toProduct().remaining)
        assertEquals(100, CatalogItemWithStock(item, original = 100, stock = 100, sold = 40).toProduct().received)
        assertEquals(0, CatalogItemWithStock(item, original = 100, stock = 100, sold = 130).toProduct().remaining)
    }

    @Test
    fun `product round trip and id is required`() {
        val product = Product(id = "p1", name = "Panadol", description = "Pain", price = 50, discountPercent = 10, received = 100, remaining = 100)
        val item = product.toItem()
        assertEquals(50, item.price)
        assertEquals(ProductType.Product, item.type)
        assertEquals(UserInventoryEntity("r1", "p1", original = 100, current = 100), product.toInventory("r1"))
        assertEquals(product, CatalogItemWithStock(item, original = 100, stock = 100, sold = 0).toProduct())
        assertEquals("panadol pain", item.searchText)
        assertTrue(runCatching { Product(name = "x").toItem() }.isFailure)
    }

    @Test
    fun `custody round trip keeps what came in and what is left`() {
        val custody = Custody("c1", "Pen", ProductType.Gift, "Blue", received = 10, remaining = 7)
        assertNull(custody.toItem().price)
        assertEquals(UserInventoryEntity("r1", "c1", original = 10, current = 7), custody.toInventory("r1"))
        assertEquals(custody, CatalogItemWithStock(custody.toItem(), original = 10, stock = 7, sold = 0).toCustody())
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
            travelSeconds = 60, noIssueFound = false, imageNotes = listOf("img", "img2"), voiceNotePath = "voice.m4a", syncStatus = SyncStatus.Synced,
            durationLabel = "10 min", status = VisitStatus.Rejected, requestedAtMillis = 1L, decidedAtMillis = 2L,
            cancelReason = "Sick", textNote = "Call back", requestId = "req1",
        )
        val back = VisitWithDetails(visit.toEntity(), null, null, emptyList(), emptyList(), emptyList()).toDomain()
        assertEquals(visit.copy(gifts = emptyList(), products = emptyList()), back)
    }

    @Test
    fun `visit takes account supervisor and lines from the relations`() {
        val accountDomain = Account("a1", name = "Clinic", accountType = BasicModel("Pharmacy", "Pharmacy"), city = BasicModel("Cairo", "Cairo"), district = BasicModel("Nasr", "Nasr"), address = "St 1", latitude = 3.0, longitude = 4.0, doctors = listOf(Doctor("d1", name = "Dr. Ali", classCategory = BasicModel("A", "A"), specialty = BasicModel("Cardio", "Cardio"))))
        val account = accountDomain.toEntity()
        val supervisor = Rep("s1", "Boss").toEntity()
        val gift = Custody("c1", "Pen", ProductType.Gift, received = 10).toItem()
        val sample = Custody("c2", "Drug", ProductType.Sample, received = 5).toItem()
        val details = VisitWithDetails(
            visit = Visit(id = "v1", accountId = "a1", supervisorId = "s1", status = VisitStatus.Scheduled).toEntity(),
            account = account.withPlaceOf(accountDomain),
            supervisor = supervisor,
            items = listOf(gift, sample),
            lines = listOf(VisitItemEntity("v1", "c1", 2), VisitItemEntity("v1", "c2", 1)),
            doctors = emptyList(),
        )
        val visit = details.toDomain()
        assertEquals("Pharmacy", visit.account?.name)
        assertEquals("Clinic", visit.placeName)
        assertEquals("Cairo", visit.city?.name)
        assertEquals("Nasr", visit.region?.name)
        assertEquals("St 1", visit.address)
        assertEquals(3.0, visit.accountLatitude, 0.0)
        assertEquals("Boss", visit.supervisor?.name)
        assertEquals(listOf("c1"), visit.gifts?.map { it.id })
        assertEquals(2, visit.gifts?.single()?.quantity)
        assertEquals(2, visit.gifts?.single()?.remaining)
        assertEquals(listOf("c2"), visit.products?.map { it.id })
        assertEquals(1, visit.products?.single()?.quantity)
    }

    @Test
    fun `visit doctors come from the relation and back to links`() {
        val doctor = Doctor("d1", name = "Dr. Ali")
        val details = VisitWithDetails(Visit(id = "v1", status = VisitStatus.Scheduled).toEntity(), null, null, emptyList(), emptyList(), listOf(doctor.toEntity()))
        assertEquals(listOf("d1"), details.toDomain().doctors.map { it.id })
        assertEquals(listOf(VisitDoctorEntity("v1", "d1")), Visit(id = "v1", doctors = listOf(doctor)).toDoctorLinks())
        assertTrue(Visit(id = "v1").toDoctorLinks().isEmpty())
    }

    @Test
    fun `visit lines come from gifts and products`() {
        val visit = Visit(id = "v1", gifts = listOf(Product(id = "c1", quantity = 2)), products = listOf(Product(id = "c2", quantity = 1)))
        assertEquals(listOf(VisitItemEntity("v1", "c1", 2), VisitItemEntity("v1", "c2", 1)), visit.toLines())
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
        assertNull(VisitWithDetails(entity, null, null, emptyList(), emptyList(), emptyList()).toDomain().dateTimeMillis)
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
        VisitStatus.entries.forEach { assertEquals(it, Visit(id = "v1", status = it).toEntity().let { e -> visitStatusOf(e.approvalStatus, e.executionStatus) }) }
    }
}
