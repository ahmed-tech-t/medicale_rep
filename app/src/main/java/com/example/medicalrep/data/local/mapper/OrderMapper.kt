package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.relation.OrderWithDetails
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem

fun OrderWithDetails.toDomain() = Order(
    id = order.id,
    number = order.number,
    backendId = order.backendId,
    repId = order.repId,
    accountId = order.accountId,
    doctorId = order.doctorId,
    status = order.status,
    accountName = account?.name,
    createdAtMillis = order.createdAtMillis,
    syncStatus = order.syncStatus,
    items = items.map(OrderItemEntity::toDomain),
)

private fun OrderItemEntity.toDomain() = OrderItem(
    productId = productId,
    productName = productName,
    unitPrice = unitPrice,
    discountPercent = discountPercent,
    quantity = quantity,
)

/** The order's own columns; its search text is made by the DAO when the order is saved. */
fun Order.toEntity() = OrderEntity(
    id = id,
    backendId = backendId,
    repId = repId,
    accountId = accountId,
    doctorId = doctorId,
    status = status,
    totalAmount = total,
    createdAtMillis = createdAtMillis,
    syncStatus = syncStatus,
    number = number,
)

fun Order.toItems(): List<OrderItemEntity> = items.map {
    OrderItemEntity(id, it.productId, it.productName, it.unitPrice, it.discountPercent, it.quantity)
}
