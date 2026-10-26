package com.example.medicalrep.ui.mapper

import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem
import com.example.medicalrep.domain.models.SyncStatus
import com.example.medicalrep.ui.models.OrderItemUi
import com.example.medicalrep.ui.models.OrderUi
import com.example.medicalrep.ui.utils.formatClockTime
import com.example.medicalrep.ui.utils.formatDate

fun Order.toUi() = OrderUi(
    id = id,
    accountName = accountName.orEmpty(),
    date = createdAtMillis.takeIf { it > 0 }?.let(::formatDate).orEmpty(),
    time = createdAtMillis.takeIf { it > 0 }?.let(::formatClockTime).orEmpty(),
    total = total,
    isSynced = syncStatus == SyncStatus.Synced,
    items = items.map { it.toUi() },
)

fun OrderItem.toUi() = OrderItemUi(
    productId = productId,
    name = productName,
    quantity = quantity,
    unitPrice = unitPrice,
    discountPercent = discountPercent,
    lineTotal = lineTotal,
)
