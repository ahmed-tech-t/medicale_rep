package com.example.medicalrep.data.local.dao

import com.example.medicalrep.domain.models.BasicModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.entity.VisitEntity
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.ExecutionStatus
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.VisitStatus
import com.example.medicalrep.domain.models.VisitType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

private const val FROM = 1_000L
private const val TO = 2_000L

/** The week's visit numbers, the accounts of today's visits by type, doctors, class counts and the quantity ordered. */
@RunWith(AndroidJUnit4::class)
class StatisticsDaoTest : DaoTestBase() {
    private val dao get() = db.statisticsDao()

    private fun account(id: String, entityType: String) = Account(id = id, name = id, accountType = entityType.opt()).toEntity()

    @Test fun completedCountsOnlyCompletedVisitsFinishedInTheRange() = runBlocking {
        save(visit("in", status = VisitStatus.Completed, dateTimeMillis = FROM, completedAtMillis = FROM))
        save(visit("end-excluded", status = VisitStatus.Completed, dateTimeMillis = FROM, completedAtMillis = TO))
        save(visit("before", status = VisitStatus.Completed, dateTimeMillis = FROM, completedAtMillis = FROM - 1))
        save(visit("todo", status = VisitStatus.Scheduled, dateTimeMillis = FROM))
        assertEquals(1, dao.completedBetween(FROM, TO).first())
    }

    @Test fun plannedCountsScheduledCompletedAndMissedDatedInTheRangeOnly() = runBlocking {
        save(visit("todo", status = VisitStatus.Scheduled, dateTimeMillis = 1_500))
        save(visit("done", status = VisitStatus.Completed, dateTimeMillis = FROM, completedAtMillis = 1_600))
        save(visit("missed", status = VisitStatus.Missed, dateTimeMillis = 1_500))
        save(visit("pending", type = VisitType.Planning, status = VisitStatus.PendingApproval, dateTimeMillis = 1_500))
        save(visit("rejected", type = VisitType.Planning, status = VisitStatus.Rejected, dateTimeMillis = 1_500))
        save(visit("tomorrow", status = VisitStatus.Scheduled, dateTimeMillis = TO))
        assertEquals(3, dao.plannedBetween(FROM, TO).first())
    }

    @Test fun countOfRepCountsOneRepAndOneStatusInTheRangeOnly() = runBlocking {
        save(visit("done-1", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = FROM))
        save(visit("done-2", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = 1_500))
        save(visit("done-other", repId = "r2", status = VisitStatus.Completed, dateTimeMillis = 1_500))
        save(visit("done-tomorrow", repId = "r1", status = VisitStatus.Completed, dateTimeMillis = TO))
        save(visit("missed", repId = "r1", status = VisitStatus.Missed, dateTimeMillis = 1_500))
        assertEquals(2, dao.countOfRep("r1", ExecutionStatus.Completed, FROM, TO).first())
        assertEquals(1, dao.countOfRep("r1", ExecutionStatus.Cancelled, FROM, TO).first())
        assertEquals(0, dao.countOfRep("r1", ExecutionStatus.Scheduled, FROM, TO).first())
        assertEquals(0, dao.countOfRep("nobody", ExecutionStatus.Completed, FROM, TO).first())
    }

    @Test fun accountsOfTodaysVisitsAreCountedOncePerTypeAndDoctorsNeedASpecialty() = runBlocking {
        db.accountDao().save(account("clinic-1", "clinic"))
        db.addDoctor("clinic-1", specialty = "Cardio")
        db.accountDao().save(account("clinic-2", "clinic"))
        db.accountDao().save(account("pharmacy-1", "pharmacy"))
        db.accountDao().save(account("missed-one", "hospital"))
        db.addDoctor("missed-one", specialty = "x")
        db.accountDao().save(account("tomorrow", "pharmacy"))
        db.addDoctor("tomorrow", specialty = "y")
        save(visit("v1", accountId = "clinic-1", status = VisitStatus.Scheduled, dateTimeMillis = 1_100))
        save(visit("v2", accountId = "clinic-1", status = VisitStatus.Completed, dateTimeMillis = 1_200, completedAtMillis = 1_300))
        save(visit("v3", accountId = "clinic-2", status = VisitStatus.Scheduled, dateTimeMillis = 1_400))
        save(visit("v4", accountId = "pharmacy-1", status = VisitStatus.Scheduled, dateTimeMillis = 1_500))
        save(visit("v5", accountId = "tomorrow", status = VisitStatus.Scheduled, dateTimeMillis = TO))
        save(visit("v6", accountId = "missed-one", status = VisitStatus.Missed, dateTimeMillis = 1_500))
        val byType = dao.visitedAccountsByType(FROM, TO).first().associate { it.accountType to it.accounts }
        assertEquals(mapOf(AccountType.Clinic to 2, AccountType.Pharmacy to 1, AccountType.Hospital to 1), byType)
        assertEquals(2, dao.visitedDoctors(FROM, TO).first())
    }

    @Test fun classCountsOnlyCoverClassesAToC() = runBlocking {
        listOf("A" to 2, "B" to 1, "C" to 3, "D" to 4, "" to 5).forEach { (grade, count) ->
            (1..count).forEach {
                db.accountDao().save(account("$grade-$it", "clinic"))
                db.addDoctor("$grade-$it", classCategory = grade)
            }
        }
        assertEquals(listOf("A" to 2, "B" to 1, "C" to 3), dao.classCounts().first().map { it.grade to it.accounts })
    }

    @Test fun productQuantitySumsWhatTheRepReceived() = runBlocking {
        assertEquals(0, dao.productQuantity().first())
        db.addStock("p1", original = 100, type = ProductType.Product)
        db.addStock("p2", original = 50, type = ProductType.Product)
        db.addStock("g1", original = 7, type = ProductType.Gift)
        assertEquals(150, dao.productQuantity().first())
    }

    @Test fun orderedQuantitySumsEveryOrderLine() = runBlocking {
        assertEquals(0, dao.orderedQuantity().first())
        db.orderDao().save(Order(id = "o1").toEntity(), listOf(OrderItemEntity("o1", "p1", "Pen", 10, 0, 3), OrderItemEntity("o1", "p2", "Cap", 5, 0, 4)))
        db.orderDao().save(Order(id = "o2").toEntity(), listOf(OrderItemEntity("o2", "p1", "Pen", 10, 0, 5)))
        assertEquals(12, dao.orderedQuantity().first())
    }

    private suspend fun save(visit: VisitEntity) = db.visitDao().save(visit, emptyList())
}
