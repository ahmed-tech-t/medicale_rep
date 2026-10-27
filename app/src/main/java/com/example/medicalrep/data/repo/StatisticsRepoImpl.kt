package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.CustodyDao
import com.example.medicalrep.data.local.dao.StatisticsDao
import com.example.medicalrep.data.local.mapper.toDomain
import com.example.medicalrep.domain.models.AccountType
import com.example.medicalrep.domain.models.ClassShare
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.models.StockCount
import com.example.medicalrep.domain.models.VisitProgress
import com.example.medicalrep.domain.repo.StatisticsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class StatisticsRepoImpl @Inject constructor(
    private val dao: StatisticsDao,
    private val custodyDao: CustodyDao,
) : StatisticsRepo {

    override fun progress(fromMillis: Long, toMillis: Long): Flow<VisitProgress> = combine(
        dao.completedBetween(fromMillis, toMillis),
        dao.plannedBetween(fromMillis, toMillis),
    ) { completed, planned -> VisitProgress(completed = completed, planned = planned) }

    override fun between(fromMillis: Long, toMillis: Long): Flow<Statistics> {
        val visits = combine(
            progress(fromMillis, toMillis),
            dao.visitedAccountsByType(fromMillis, toMillis),
            dao.visitedDoctors(fromMillis, toMillis),
        ) { progress, types, doctors ->
            val accountsOf = types.associate { it.accountType to it.accounts }
            Statistics(
                completedVisits = progress.completed,
                plannedVisits = progress.planned,
                doctors = doctors,
                clinics = accountsOf[AccountType.Clinic] ?: 0,
                hospitals = accountsOf[AccountType.Hospital] ?: 0,
                pharmacies = accountsOf[AccountType.Pharmacy] ?: 0,
                medicalCenters = accountsOf[AccountType.MedicalCenter] ?: 0,
            )
        }
        // Gifts are custody of kind Gift, samples custody of kind Product; products are the ones for sale (received, sold by orders).
        val stock = combine(
            custodyDao.getByKind(ProductType.Gift),
            custodyDao.getByKind(ProductType.Product),
            dao.orderedQuantity(),
            dao.classCounts(),
            dao.productQuantity(),
        ) { gifts, samples, sold, classes, received ->
            Statistics(
                gifts = StockCount.of(gifts.map { it.toDomain() }),
                samples = StockCount.of(samples.map { it.toDomain() }),
                products = StockCount.of(total = received, used = sold),
                classes = classes.map { ClassShare(it.grade, it.accounts) },
            )
        }
        return combine(visits, stock) { inRange, carried ->
            inRange.copy(gifts = carried.gifts, samples = carried.samples, products = carried.products, classes = carried.classes)
        }
    }
}
