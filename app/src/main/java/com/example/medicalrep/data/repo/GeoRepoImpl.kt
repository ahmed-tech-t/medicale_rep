package com.example.medicalrep.data.repo

import com.example.medicalrep.data.local.dao.GeoDao
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.District
import com.example.medicalrep.domain.repo.GeoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GeoRepoImpl @Inject constructor(private val dao: GeoDao) : GeoRepo {
    override fun cities(): Flow<List<BasicModel>> = dao.cities().map { list -> list.map { BasicModel(it.id, it.name) } }

    override fun districts(cityId: String?): Flow<List<BasicModel>> =
        dao.districts(cityId.orEmpty()).map { list -> list.map { BasicModel(it.id, it.name) } }

    override fun districtsOf(repId: String): Flow<List<BasicModel>> =
        dao.districtsOf(repId).map { list -> list.map { BasicModel(it.id, it.name) } }

    override suspend fun save(cities: List<BasicModel>, districts: List<District>) {
        dao.upsertCities(cities.map { CityEntity(it.id, it.name) })
        dao.upsertDistricts(districts.map { DistrictEntity(it.id, it.cityId, it.name) })
    }

    override suspend fun assign(repId: String, districtIds: List<String>) = dao.assign(repId, districtIds)
}
