package com.example.medicalrep.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity

/** An order with its account and lines. */
data class OrderWithDetails(
    @Embedded val order: OrderEntity,
    @Relation(parentColumn = "accountId", entityColumn = "id")
    val account: AccountEntity?,
    @Relation(parentColumn = "id", entityColumn = "orderId")
    val items: List<OrderItemEntity>,
)
