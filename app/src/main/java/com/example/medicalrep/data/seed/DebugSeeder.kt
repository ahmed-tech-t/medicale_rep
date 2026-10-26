package com.example.medicalrep.data.seed

import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.domain.repo.SyncInfoRepo
import com.example.medicalrep.domain.repo.UserRepo
import com.example.medicalrep.domain.repo.VisitRepo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Debug builds only: fills an empty database with sample data until the server sync exists.
 * It runs once; the marker is stored with the other sync times.
 */
@Singleton
class DebugSeeder @Inject constructor(
    private val userRepo: UserRepo,
    private val accountRepo: AccountRepo,
    private val productRepo: ProductRepo,
    private val visitRepo: VisitRepo,
    private val syncInfoRepo: SyncInfoRepo,
) {

    suspend fun seedIfEmpty(now: Long = System.currentTimeMillis()) {
        if (syncInfoRepo.get(SEED_KEY) != null) return
        val data = SeedDataFactory.build(now)
        // Order matters: visits point at users and accounts.
        data.users.forEach { userRepo.save(it) }
        data.accounts.forEach { accountRepo.save(it) }
        productRepo.saveProducts(data.products)
        productRepo.saveCustody(data.custody)
        data.visits.forEach { visitRepo.save(it) }
        syncInfoRepo.set(SEED_KEY, now)
    }

    companion object {
        const val SEED_KEY = "debug_seed_v2"
    }
}
