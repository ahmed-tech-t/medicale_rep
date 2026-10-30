package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.District
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory cities and districts for ViewModel tests. */
class FakeGeoRepo(
    cities: List<BasicModel> = emptyList(),
    districts: List<District> = emptyList(),
) : GeoRepo {
    private val cityList = MutableStateFlow(cities)
    private val districtList = MutableStateFlow(districts)

    override fun cities(): Flow<List<BasicModel>> = cityList

    override fun districts(cityId: String?): Flow<List<BasicModel>> =
        districtList.map { list -> list.filter { cityId == null || it.cityId == cityId }.map { BasicModel(it.id, it.name) } }

    override fun districtsOf(repId: String): Flow<List<BasicModel>> = MutableStateFlow(emptyList())

    override suspend fun save(cities: List<BasicModel>, districts: List<District>) {
        cityList.value = cities
        districtList.value = districts
    }

    override suspend fun assign(repId: String, districtIds: List<String>) = Unit
}
