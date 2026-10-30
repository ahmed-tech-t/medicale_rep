package com.example.medicalrep.data.seed

import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Shift
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.domain.models.RepRole
import com.example.medicalrep.domain.models.VisitMode
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The debug seed must be usable by every wired screen:
 * 1. Ids are unique in every list.
 * 2. Every reference exists: account -> supervisor, visit -> account / rep / supervisor, visit line -> custody.
 * 3. All four account types, three classes, several cities, districts and specialties are present.
 * 6. Three orders numbered 999 to 1001 with real accounts, rep and product lines.
 * 4. Products exist; custody has gifts and samples with stock left.
 * 5. Visits cover: planned (both shifts), done synced and not synced, double visits with a supervisor,
 *    pending and rejected plan requests (with a reason), and dates relative to "now".
 */
class SeedDataTest {

    private val now = 1_792_000_000_000L
    private val data = SeedDataFactory.build(now)

    @Test
    fun `there is enough data to test lists and paging`() {
        assertEquals(50, data.accounts.size)
        assertEquals(200, data.products.size)
        assertTrue(data.visits.size >= 100)
        // Every kind of visit is well represented, so every tab and filter has several pages' worth of variety.
        assertTrue(data.visits.count { it.status == VisitStatus.Scheduled } >= 15)
        assertTrue(data.visits.count { it.status == VisitStatus.Completed } >= 40)
        assertTrue(data.visits.count { it.status == VisitStatus.PendingApproval } >= 8)
        assertTrue(data.products.map { it.name }.toSet().size == 200)
    }

    @Test
    fun `every district of every city has an account of every type so the place list is never empty`() {
        val existing = data.accounts.map { Triple(it.city?.id, it.district?.id, it.accountType?.id) }.toSet()
        val types = data.accounts.map { it.accountType?.id }.toSet()
        val missing = data.districts.flatMap { d -> types.map { t -> Triple(d.cityId, d.id, t) } } - existing
        assertEquals(emptyList<Triple<String?, String?, String?>>(), missing)
    }

    @Test
    fun `every account except a pharmacy has a doctor and every doctor works at a real account`() {
        val accountIds = data.accounts.map { it.id }.toSet()
        assertTrue(data.doctors.all { placement -> placement.accountIds.isNotEmpty() && accountIds.containsAll(placement.accountIds) })
        val withDoctors = data.doctors.flatMap { it.accountIds }.toSet()
        val withoutDoctors = data.accounts.filter { it.id !in withDoctors }
        assertTrue(withoutDoctors.all { it.accountType?.name == "صيدلية" })
    }

    @Test
    fun `districts belong to seeded cities and every account sits in a district of its own city`() {
        val cityIds = data.cities.map { it.id }.toSet()
        assertTrue(data.districts.all { it.cityId in cityIds })
        val cityOfDistrict = data.districts.associate { it.id to it.cityId }
        assertTrue(data.accounts.all { cityOfDistrict[it.district?.id] == it.city?.id })
    }

    @Test
    fun `orders are numbered, belong to the rep and use real accounts and products`() {
        assertEquals(listOf(999, 1000, 1001), data.orders.map { it.number })
        assertEquals(data.orders.size, data.orders.map { it.id }.toSet().size)
        val accountIds = data.accounts.map { it.id }.toSet()
        val userIds = data.reps.map { it.id }.toSet()
        val products = data.products.associateBy { it.id }
        data.orders.forEach { order ->
            assertTrue(order.accountId in accountIds)
            assertTrue(order.repId in userIds)
            assertTrue(order.createdAtMillis in 1..now)
            assertTrue(order.items.isNotEmpty() && order.total > 0)
            order.items.forEach { line ->
                val product = products.getValue(line.productId)
                assertEquals(product.name, line.productName)
                assertEquals(product.price, line.unitPrice)
                assertEquals(product.discountPercent, line.discountPercent)
            }
        }
    }

    @Test
    fun `every product has a quantity and the orders never sell more than the rep received`() {
        assertTrue(data.products.all { it.received > 0 })
        val sold = data.orders.flatMap { it.items }.groupBy { it.productId }.mapValues { (_, lines) -> lines.sumOf { it.quantity } }
        val received = data.products.associate { it.id to it.received }
        sold.forEach { (productId, quantity) -> assertTrue(quantity <= received.getValue(productId)) }
    }

    @Test
    fun `ids are unique`() {
        assertEquals(data.reps.size, data.reps.map { it.id }.toSet().size)
        assertEquals(data.accounts.size, data.accounts.map { it.id }.toSet().size)
        assertEquals(data.products.size, data.products.map { it.id }.toSet().size)
        assertEquals(data.custody.size, data.custody.map { it.id }.toSet().size)
        assertEquals(data.visits.size, data.visits.map { it.id }.toSet().size)
    }

    @Test
    fun `every reference points at an existing row`() {
        val supervisors = data.reps.filter { it.role == RepRole.Supervisor }.map { it.id }.toSet()
        val reps = data.reps.filter { it.role == RepRole.Rep }
        assertTrue(supervisors.isNotEmpty() && reps.isNotEmpty())
        assertTrue(reps.all { it.supervisorId in supervisors })
        assertTrue(data.accounts.all { it.supervisorId in supervisors })
        val accountIds = data.accounts.map { it.id }.toSet()
        val repIds = reps.map { it.id }.toSet()
        val custodyIds = data.custody.map { it.id }.toSet()
        data.visits.forEach { visit ->
            assertTrue(visit.accountId in accountIds)
            assertTrue(visit.repId in repIds)
            assertTrue(visit.supervisorId == null || visit.supervisorId in supervisors)
            (visit.gifts.orEmpty() + visit.products.orEmpty()).forEach { assertTrue(it.id in custodyIds) }
        }
    }

    @Test
    fun `accounts cover every type, class and several places`() {
        val types = data.accounts.mapNotNull { AccountType.fromEntityType(it.accountType?.name.orEmpty()) }.toSet()
        assertEquals(AccountType.entries.toSet(), types)
        assertEquals(setOf("A", "B", "C"), data.doctors.map { it.doctor.classCategory?.name }.toSet())
        assertTrue(data.accounts.map { it.city }.toSet().size >= 2)
        assertTrue(data.accounts.map { it.district }.toSet().size >= 2)
        assertTrue(data.doctors.map { it.doctor.specialty }.toSet().size >= 3)
        assertTrue(data.accounts.all { it.syncStatus == SyncStatus.Synced })
    }

    @Test
    fun `products and custody have stock`() {
        assertTrue(data.products.size >= 5 && data.products.all { it.price > 0 })
        assertEquals(setOf(ProductType.Gift, ProductType.Sample), data.custody.map { it.kind }.toSet())
        assertTrue(data.custody.all { it.remaining > 0 })
    }

    @Test
    fun `visits cover the filters of every screen`() {
        val planned = data.visits.filter { it.status == VisitStatus.Scheduled }
        assertTrue(planned.size >= 4)
        assertEquals(setOf(Shift.Am, Shift.Pm), planned.map { it.shift }.toSet())
        val done = data.visits.filter { it.status == VisitStatus.Completed }
        assertTrue(done.any { it.type == VisitType.Planned } && done.any { it.type == VisitType.Unplanned })
        assertEquals(setOf(SyncStatus.Synced, SyncStatus.NotSynced, SyncStatus.Failed), done.map { it.syncStatus }.toSet())
        assertEquals(setOf(Shift.Am, Shift.Pm), done.map { it.shift }.toSet())
        val double = data.visits.filter { it.visitMode == VisitMode.Double }
        assertTrue(double.isNotEmpty() && double.all { it.supervisorId != null })
        val requests = data.visits.filter { it.type == VisitType.Planning }
        assertEquals(setOf(VisitStatus.PendingApproval, VisitStatus.Rejected), requests.map { it.status }.toSet())
        assertTrue(requests.all { it.requestedAtMillis != null })
    }

    @Test
    fun `done visits are in the past and planned ones are today or later`() {
        data.visits.filter { it.status == VisitStatus.Completed }.forEach {
            assertTrue(it.completedAtMillis!! < now && it.completedAtMillis!! >= it.dateTimeMillis!!)
        }
        data.visits.filter { it.status == VisitStatus.Scheduled }
            .forEach { assertTrue(it.dateTimeMillis!! >= now - 12 * HOUR) }
    }

    @Test
    fun `every visit has a status that fits its type and its dates`() {
        assertTrue(data.visits.all { it.status != null })
        // Only done visits carry a completion time.
        data.visits.forEach { assertEquals(it.status == VisitStatus.Completed, it.completedAtMillis != null) }
        // Plan requests wait for or were refused by the admin; the other types never are.
        data.visits.filter { it.type == VisitType.Planning }
            .forEach { assertTrue(it.status in setOf(VisitStatus.PendingApproval, VisitStatus.Rejected)) }
        data.visits.filter { it.type != VisitType.Planning }
            .forEach { assertTrue(it.status in setOf(VisitStatus.Scheduled, VisitStatus.Completed, VisitStatus.Missed)) }
        // Missed visits are planned ones whose day passed, and the lists have some of them.
        val missed = data.visits.filter { it.status == VisitStatus.Missed }
        assertTrue(missed.size >= 5)
        missed.forEach { assertTrue(it.type == VisitType.Planned && it.dateTimeMillis!! < now) }
        assertTrue(data.visits.count { it.status == VisitStatus.Rejected } >= 3)
    }

    @Test
    fun `every rep has a track on each of the last five days with three or four visits on it`() {
        reps().forEach { rep ->
            (0 until 5).forEach { day ->
                val visits = trackVisits(rep.id, day)
                assertTrue("${rep.id} day $day: ${visits.size} visits", visits.size in 3..4)
                assertTrue("${rep.id} day $day: ${pointsOf(rep.id, day).size} points", pointsOf(rep.id, day).size >= 10)
                assertEquals(visits.size, visits.map { it.accountId }.toSet().size)
            }
        }
    }

    @Test
    fun `the track of a day goes through the place of each visit while it was done`() {
        val accounts = data.accounts.associateBy { it.id }
        reps().forEach { rep ->
            (0 until 5).forEach { day ->
                val points = pointsOf(rep.id, day)
                trackVisits(rep.id, day).forEach { visit ->
                    val account = accounts.getValue(requireNotNull(visit.accountId))
                    val atVisit = points.filter { it.visitId == visit.id }
                    assertTrue(atVisit.isNotEmpty())
                    atVisit.forEach {
                        assertEquals(account.latitude, it.latitude, 0.0)
                        assertEquals(account.longitude, it.longitude, 0.0)
                        assertTrue(it.timeMillis >= visit.dateTimeMillis!! && it.timeMillis <= visit.completedAtMillis!!)
                    }
                }
            }
        }
    }

    @Test
    fun `the rep moves between two visits and starts moving before the first one`() {
        reps().forEach { rep ->
            (0 until 5).forEach { day ->
                val points = pointsOf(rep.id, day)
                val visits = trackVisits(rep.id, day).sortedBy { it.dateTimeMillis }
                assertTrue(points.any { it.visitId == null && it.timeMillis < visits.first().dateTimeMillis!! })
                visits.zipWithNext().forEach { (previous, next) ->
                    assertTrue(next.dateTimeMillis!! > previous.completedAtMillis!!)
                    val between = points.filter { it.timeMillis > previous.completedAtMillis!! && it.timeMillis < next.dateTimeMillis!! }
                    assertTrue(between.size >= 5 && between.all { it.visitId == null })
                }
            }
        }
    }

    @Test
    fun `the track starts and ends away from the places of the visits`() {
        val places = data.accounts.associateBy { it.id }
        reps().forEach { rep ->
            (0 until 5).forEach { day ->
                val points = pointsOf(rep.id, day).sortedBy { it.timeMillis }
                val visitPlaces = trackVisits(rep.id, day).map { places.getValue(requireNotNull(it.accountId)) }
                listOf(points.first(), points.last()).forEach { end ->
                    assertTrue(visitPlaces.none { it.latitude == end.latitude && it.longitude == end.longitude })
                }
            }
        }
    }

    @Test
    fun `track points belong to reps, are in the past and never share a rep and a time`() {
        val repIds = reps().map { it.id }.toSet()
        assertTrue(data.trackPoints.all { it.repId in repIds && it.timeMillis <= now })
        assertEquals(data.trackPoints.size, data.trackPoints.map { it.repId to it.timeMillis }.toSet().size)
    }

    private fun reps() = data.reps.filter { it.role == RepRole.Rep }

    private fun trackVisits(repId: String, day: Int) = data.visits.filter { it.id.orEmpty().startsWith("v-track-$repId-$day-") }

    private fun pointsOf(repId: String, day: Int) =
        data.trackPoints.filter { it.repId == repId && it.timeMillis > now - (day + 1) * DAY && it.timeMillis <= now - day * DAY }

    private companion object {
        const val HOUR = 3_600_000L
        const val DAY = 24 * HOUR
    }
}
