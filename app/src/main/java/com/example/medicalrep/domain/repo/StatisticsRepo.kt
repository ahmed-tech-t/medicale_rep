package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Statistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepo {
    /** The numbers for the day from [fromMillis] (included) to [toMillis] (not included); they change as the data does. */
    fun between(fromMillis: Long, toMillis: Long): Flow<Statistics>
}
