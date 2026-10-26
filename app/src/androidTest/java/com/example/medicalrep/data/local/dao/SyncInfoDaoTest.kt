package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.example.medicalrep.data.local.entity.SyncInfoEntity

/** CRUD and search of the Data Center update times. */
@RunWith(AndroidJUnit4::class)
class SyncInfoDaoTest : DaoTestBase() {
    private val dao get() = db.syncInfoDao()

    @Test fun upsertReplacesTheSameKey() = runBlocking {
        dao.upsert(SyncInfoEntity("products", 1))
        dao.upsert(SyncInfoEntity("products", 2))
        assertEquals(2L, dao.getById("products")?.lastUpdatedMillis)
        assertEquals(1, dao.getAll().first().size)
    }

    @Test fun deleteAndClear() = runBlocking {
        dao.insertAll(listOf(SyncInfoEntity("a", 1), SyncInfoEntity("b", 1)))
        assertEquals(1, dao.deleteById("a"))
        assertNull(dao.getById("a"))
        dao.clear()
        assertTrue(dao.getAll().first().isEmpty())
    }

    @Test fun searchByKey() = runBlocking {
        dao.insertAll(listOf(SyncInfoEntity("products", 1), SyncInfoEntity("accounts", 1)))
        assertEquals(listOf("products"), dao.search("prod").allRows().map { it.key })
        assertTrue(dao.search("zzz").allRows().isEmpty())
    }
}
