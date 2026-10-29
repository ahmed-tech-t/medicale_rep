package com.example.medicalrep.ui.screens.planvisit.utils

import com.example.medicalrep.ui.models.VisitUi

/** "city - address" of the visit's account, without the blank parts. */
fun VisitUi.accountAddressLine(): String =
    listOfNotNull(city?.name, address).filter { it.isNotBlank() }.joinToString(" - ")
