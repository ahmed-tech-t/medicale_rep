package com.example.medicalrep.ui.screens.planvisit.utils

import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.ui.models.ProductUi

/** What the add-item sheet shows for a custody row: one piece to start with, and how many are left. */
fun Custody.toCatalogItem() = ProductUi(
    id = id,
    name = name,
    quantity = 1,
    kind = kind,
    remaining = remaining,
    description = description,
)
