package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.mapper.toEntity
import com.example.medicalrep.domain.models.Order
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/** The signed-in rep is not a row in `reps`, yet his visits, orders and track points carry his id and are stored. */
@RunWith(AndroidJUnit4::class)
class RepIdWithoutRepTest : DaoTestBase() {

    @Test fun aVisitIsStoredForARepThatIsNotInTheRepsTable() = runBlocking {
        db.visitDao().save(visit("v1", repId = "signed-in"), emptyList())
        assertEquals("signed-in", db.visitDao().getById("v1")?.repId)
    }

    @Test fun anOrderIsStoredForARepThatIsNotInTheRepsTable() = runBlocking {
        db.orderDao().save(Order(id = "o1", repId = "signed-in").toEntity(), listOf(OrderItemEntity("o1", "p1", "Pen", 10, 0, 1)))
        assertEquals("signed-in", db.orderDao().getById("o1")?.repId)
        assertNotNull(db.orderDao().getWithDetails("o1"))
    }

    @Test fun aTrackPointIsStoredForARepThatIsNotInTheRepsTable() = runBlocking {
        val id = db.trackPointDao().insert(point("signed-in", 100))
        assertEquals(100L, db.trackPointDao().getById(id)?.timeMillis)
        assertEquals(1, db.trackPointDao().getByRepAndDay("signed-in", 0, 500).first().size)
    }
}
