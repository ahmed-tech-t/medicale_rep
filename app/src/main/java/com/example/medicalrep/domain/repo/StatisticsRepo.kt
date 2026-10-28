package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.models.VisitProgress
import kotlinx.coroutines.flow.Flow

interface StatisticsRepo {
    /** The visits done out of the visits planned in the range (from included, to not included). */
    fun progress(fromMillis: Long, toMillis: Long): Flow<VisitProgress>

    /** The same for one rep: [fromMillis] included, [toMillis] not included. */
    fun progressOf(repId: String, fromMillis: Long, toMillis: Long): Flow<VisitProgress>

    /** The numbers for the day from [fromMillis] (included) to [toMillis] (not included); they change as the data does. */
    fun between(fromMillis: Long, toMillis: Long): Flow<Statistics>
}
