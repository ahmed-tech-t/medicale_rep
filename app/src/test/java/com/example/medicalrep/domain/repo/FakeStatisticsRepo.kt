package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Statistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory statistics for ViewModel tests; remembers the day range it was asked for. */
class FakeStatisticsRepo(initial: Statistics = Statistics()) : StatisticsRepo {
    val current = MutableStateFlow(initial)

    /** The (from, to) of every `between` call, in order. */
    val requestedRanges = mutableListOf<Pair<Long, Long>>()

    override fun between(fromMillis: Long, toMillis: Long): Flow<Statistics> {
        requestedRanges += fromMillis to toMillis
        return current
    }
}
