package com.example.medicalrep.ui.screens.statistics.utils

import com.example.medicalrep.domain.models.ClassShare
import com.example.medicalrep.domain.models.Statistics
import com.example.medicalrep.domain.models.StockCount
import com.example.medicalrep.ui.screens.statistics.main.StatisticsState

/** Sample numbers for previews. */
object StatisticsFakes {
    fun state() = StatisticsState(
        Statistics(
            completedVisits = 10,
            plannedVisits = 26,
            doctors = 14,
            clinics = 6,
            hospitals = 2,
            pharmacies = 3,
            medicalCenters = 1,
            gifts = StockCount(total = 90, used = 62, remaining = 28),
            samples = StockCount(total = 250, used = 165, remaining = 85),
            products = StockCount(total = 1200, used = 860, remaining = 340),
            classes = listOf(ClassShare("A", 18), ClassShare("B", 12), ClassShare("C", 6)),
        ),
    )
}
