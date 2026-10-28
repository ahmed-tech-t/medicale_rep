package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.models.VisitProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory statistics for ViewModel tests; remembers the ranges it was asked for. */
class FakeStatisticsRepo(initial: Statistics = Statistics()) : StatisticsRepo {
    val current = MutableStateFlow(initial)
    val progress = MutableStateFlow(VisitProgress())

    /** The (from, to) of every `between` call, in order. */
    val requestedRanges = mutableListOf<Pair<Long, Long>>()

    /** The (from, to) of every `progress` call, in order. */
    val requestedProgressRanges = mutableListOf<Pair<Long, Long>>()

    val repProgress = MutableStateFlow(VisitProgress())

    /** Every `progressOf` call, in order. */
    val requestedRepProgress = mutableListOf<RepDayRequest>()

    override fun progressOf(repId: String, fromMillis: Long, toMillis: Long): Flow<VisitProgress> {
        requestedRepProgress += RepDayRequest(repId, fromMillis, toMillis)
        return repProgress
    }

    override fun between(fromMillis: Long, toMillis: Long): Flow<Statistics> {
        requestedRanges += fromMillis to toMillis
        return current
    }

    override fun progress(fromMillis: Long, toMillis: Long): Flow<VisitProgress> {
        requestedProgressRanges += fromMillis to toMillis
        return progress
    }
}
