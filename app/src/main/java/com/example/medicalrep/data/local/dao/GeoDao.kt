package com.example.medicalrep.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.entity.UserDistrictEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GeoDao {
    @Upsert
    abstract suspend fun upsertCities(cities: List<CityEntity>)

    @Upsert
    abstract suspend fun upsertDistricts(districts: List<DistrictEntity>)

    @Query("SELECT * FROM cities ORDER BY name")
    abstract fun cities(): Flow<List<CityEntity>>

    /** The districts of a city; a blank [cityId] means every district. */
    @Query("SELECT * FROM districts WHERE :cityId = '' OR cityId = :cityId ORDER BY name")
    abstract fun districts(cityId: String): Flow<List<DistrictEntity>>

    @Query("SELECT d.* FROM districts d JOIN user_districts u ON u.districtId = d.id WHERE u.repId = :repId ORDER BY d.name")
    abstract fun districtsOf(repId: String): Flow<List<DistrictEntity>>

    @Query("DELETE FROM cities WHERE id = :id")
    abstract suspend fun deleteCity(id: String): Int

    @Query("DELETE FROM user_districts WHERE repId = :repId")
    abstract suspend fun clearDistrictsOf(repId: String)

    @Upsert
    abstract suspend fun upsertUserDistricts(links: List<UserDistrictEntity>)

    /** The rep covers exactly [districtIds] afterwards. */
    @Transaction
    open suspend fun assign(repId: String, districtIds: List<String>) {
        clearDistrictsOf(repId)
        upsertUserDistricts(districtIds.distinct().map { UserDistrictEntity(repId, it) })
    }
}
