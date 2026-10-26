package com.example.medicalrep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * A product and quantity in an order. There is no foreign key to products on purpose: replacing a product row
 * during a download must never delete lines of orders that are not uploaded yet.
 */
@Entity(
    tableName = "order_items",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(OrderEntity::class, parentColumns = ["id"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("productId")],
)
data class OrderItemEntity(
    val orderId: String,
    val productId: String,
    val productName: String,
    val unitPrice: Int,
    val discountPercent: Int,
    val quantity: Int,
)
