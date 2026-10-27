package com.example.medicalrep.domain.models

/** An order a rep takes for an account; it is sent to the server and no answer comes back. */
data class Order(
    val id: String,
    /** The number the rep sees ("Order 1001"); the database gives a new order the next one. */
    val number: Int = 0,
    val backendId: String? = null,
    val repId: String? = null,
    val accountId: String? = null,
    /** Read from the account; null when the account was deleted. */
    val accountName: String? = null,
    val createdAtMillis: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.NotSynced,
    val items: List<OrderItem> = emptyList(),
) {
    val total: Double get() = items.sumOf(OrderItem::lineTotal)
}

/** A product and quantity in an order; name, price and discount are copied when the order is made. */
data class OrderItem(
    val productId: String,
    val productName: String,
    val unitPrice: Int,
    val discountPercent: Int = 0,
    val quantity: Int,
) {
    val lineTotal: Double get() = unitPrice * quantity * (100 - discountPercent) / 100.0
}
