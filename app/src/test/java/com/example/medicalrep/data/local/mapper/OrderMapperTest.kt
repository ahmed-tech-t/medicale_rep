package com.example.medicalrep.data.local.mapper

import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.relation.OrderWithDetails
import com.example.medicalrep.domain.models.Account
import com.example.medicalrep.domain.models.Order
import com.example.medicalrep.domain.models.OrderItem
import com.example.medicalrep.domain.models.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 1. An order and its items map there and back without loss.
 * 2. The account name is read from the account; a deleted account gives null.
 * 3. Line total applies the discount (decimals kept); the order total adds the lines; no items gives zero.
 * 4. The order number is stored with the order.
 */
class OrderMapperTest {

    private val order = Order(
        id = "o1", number = 1001, backendId = "b1", repId = "r1", accountId = "a1", accountName = "Clinic", createdAtMillis = 5L,
        syncStatus = SyncStatus.Synced,
        items = listOf(OrderItem("p1", "Panadol", 50, 10, 2), OrderItem("p2", "Brufen", 20, 0, 1)),
    )

    @Test
    fun `order and items round trip`() {
        val account: AccountEntity = Account("a1", name = "Clinic").toEntity()
        val back = OrderWithDetails(order.toEntity(), account, order.toItems()).toDomain()
        assertEquals(order, back)
    }

    @Test
    fun `deleted account gives no account name`() {
        assertNull(OrderWithDetails(order.toEntity(), null, order.toItems()).toDomain().accountName)
    }

    @Test
    fun `totals apply the discount`() {
        assertEquals(90.0, order.items[0].lineTotal, 0.0)
        assertEquals(110.0, order.total, 0.0)
        assertEquals(0.0, Order(id = "o").total, 0.0)
    }

    @Test
    fun `a discount keeps the decimals`() {
        assertEquals(1388.8, OrderItem("p", "Panadol", 434, 20, 4).lineTotal, 0.0001)
    }
}
