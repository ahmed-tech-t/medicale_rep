package com.example.medicalrep.domain.repo

import com.example.medicalrep.domain.models.BasicModel
import com.example.medicalrep.domain.models.District
import kotlinx.coroutines.flow.Flow

/** Cities, their districts, and the districts each rep covers. */
interface GeoRepo {
    fun cities(): Flow<List<BasicModel>>

    /** The districts of [cityId]; null means every district. */
    fun districts(cityId: String? = null): Flow<List<BasicModel>>

    fun districtsOf(repId: String): Flow<List<BasicModel>>

    suspend fun save(cities: List<BasicModel>, districts: List<District>)

    /** The rep covers exactly [districtIds] afterwards. */
    suspend fun assign(repId: String, districtIds: List<String>)
}
