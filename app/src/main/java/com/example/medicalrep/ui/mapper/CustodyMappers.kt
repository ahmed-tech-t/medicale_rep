package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Custody
import com.example.medicalrep.domain.models.ProductType
import com.example.medicalrep.ui.models.CustodyUi

fun Custody.toUi() = CustodyUi(
    id = id,
    name = name,
    isGift = kind == ProductType.Gift,
    description = description,
    received = received,
    dispatched = dispatched,
    pending = pending,
    remaining = remaining,
)
