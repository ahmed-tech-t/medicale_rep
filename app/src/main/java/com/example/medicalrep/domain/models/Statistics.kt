package com.example.medicalrep.domain.models

import androidx.compose.runtime.Immutable
import kotlin.math.roundToInt

/** Three counts of one stock: all that came in, how much of it went out, and what is left. */
@Immutable
data class StockCount(val total: Int, val used: Int, val remaining: Int) {
    companion object {
        /** What came in is the sum of what was received; what is left already loses what is in visits not uploaded yet. */
        fun of(custody: List<Custody>): StockCount {
            val total = custody.sumOf(Custody::received)
            val remaining = custody.sumOf(Custody::remaining)
            return StockCount(total = total, used = total - remaining, remaining = remaining)
        }

        /** A stock of [total] pieces of which [used] went out; what is left never goes below zero. */
        fun of(total: Int, used: Int) = StockCount(total = total, used = used, remaining = (total - used).coerceAtLeast(0))
    }
}

/** How many accounts are of one class ("A", "B", "C"). */
@Immutable
data class ClassShare(val grade: String, val accounts: Int)

/** The numbers of the Statistics page: the week's visits, the stock the rep carries, and the accounts by class. */
@Immutable
data class Statistics(
    /** Visits finished in the week, and all the week's visits that were planned (still to do, done or missed). */
    val completedVisits: Int = 0,
    val plannedVisits: Int = 0,
    /** Accounts of the week's visits: how many have a specialty (doctors) and how many are of each type. */
    val doctors: Int = 0,
    val clinics: Int = 0,
    val hospitals: Int = 0,
    val pharmacies: Int = 0,
    val medicalCenters: Int = 0,
    val gifts: StockCount = StockCount(0, 0, 0),
    val samples: StockCount = StockCount(0, 0, 0),
    /** Products for sale: what the rep received, what orders sold, and what is left. */
    val products: StockCount = StockCount(0, 0, 0),
    val classes: List<ClassShare> = emptyList(),
) {
    val totalAccounts: Int get() = classes.sumOf(ClassShare::accounts)

    /** The share of all accounts that [share] holds, as a whole percent; 0 when there are no accounts. */
    fun percentOf(share: ClassShare): Int =
        if (totalAccounts == 0) 0 else (share.accounts * 100f / totalAccounts).roundToInt()
}
