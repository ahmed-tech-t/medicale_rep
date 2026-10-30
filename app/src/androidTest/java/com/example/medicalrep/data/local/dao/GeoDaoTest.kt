package com.example.medicalrep.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Cities hold districts, districts hold accounts, and a rep covers several districts. */
@RunWith(AndroidJUnit4::class)
class GeoDaoTest : DaoTestBase() {
    private val dao get() = db.geoDao()

    @Before
    fun seed() = runBlocking {
        dao.upsertCities(listOf(CityEntity("c2", "منوف"), CityEntity("c1", "شبين الكوم")))
        dao.upsertDistricts(
            listOf(
                DistrictEntity("d1", "c1", "وسط البلد"),
                DistrictEntity("d2", "c1", "البر الشرقى"),
                DistrictEntity("d3", "c2", "وسط البلد"),
            ),
        )
    }

    @Test fun citiesAreSortedByName() = runBlocking {
        assertEquals(listOf("شبين الكوم", "منوف").sorted(), dao.cities().first().map { it.name })
    }

    @Test fun aCityListsOnlyItsDistricts() = runBlocking {
        assertEquals(listOf("d1", "d2"), dao.districts("c1").first().map { it.id }.sorted())
        assertEquals(listOf("d3"), dao.districts("c2").first().map { it.id })
    }

    @Test fun aBlankCityListsEveryDistrict() = runBlocking {
        assertEquals(3, dao.districts("").first().size)
    }

    @Test fun deletingACityDeletesItsDistrictsButKeepsItsAccounts() = runBlocking {
        db.accountDao().insert(account("a1", districtId = "d1"))
        dao.deleteCity("c1")
        assertEquals(listOf("d3"), dao.districts("").first().map { it.id })
        assertNull(db.accountDao().getById("a1")!!.account.districtId)
    }

    @Test fun aRepCoversSeveralDistrictsAndAssigningReplacesThem() = runBlocking {
        db.repDao().insert(rep("r1"))
        dao.assign("r1", listOf("d1", "d3"))
        assertEquals(listOf("d1", "d3"), dao.districtsOf("r1").first().map { it.id }.sorted())
        dao.assign("r1", listOf("d2", "d2"))
        assertEquals(listOf("d2"), dao.districtsOf("r1").first().map { it.id })
    }

    @Test fun deletingARepRemovesOnlyItsLinks() = runBlocking {
        db.repDao().insert(rep("r1"))
        dao.assign("r1", listOf("d1"))
        db.repDao().delete(rep("r1"))
        assertEquals(emptyList<String>(), dao.districtsOf("r1").first().map { it.id })
        assertEquals(3, dao.districts("").first().size)
    }
}
