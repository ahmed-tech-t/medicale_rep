package com.example.medicalrep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicalrep.data.local.dao.AccountDao
import com.example.medicalrep.data.local.dao.CatalogDao
import com.example.medicalrep.data.local.dao.NotificationDao
import com.example.medicalrep.data.local.dao.RepRequestDao
import com.example.medicalrep.data.local.dao.SystemSettingDao
import com.example.medicalrep.data.local.entity.NotificationEntity
import com.example.medicalrep.data.local.entity.RepRequestEntity
import com.example.medicalrep.data.local.entity.SystemSettingEntity
import com.example.medicalrep.data.local.dao.DoctorDao
import com.example.medicalrep.data.local.dao.DraftVisitDao
import com.example.medicalrep.data.local.dao.OrderDao
import com.example.medicalrep.data.local.dao.StatisticsDao
import com.example.medicalrep.data.local.dao.SyncInfoDao
import com.example.medicalrep.data.local.dao.TrackPointDao
import com.example.medicalrep.data.local.dao.RepDao
import com.example.medicalrep.data.local.dao.VisitDao
import com.example.medicalrep.data.local.entity.AccountDoctorEntity
import com.example.medicalrep.data.local.dao.GeoDao
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CityEntity
import com.example.medicalrep.data.local.entity.DistrictEntity
import com.example.medicalrep.data.local.entity.UserDistrictEntity
import com.example.medicalrep.data.local.entity.CatalogItemEntity
import com.example.medicalrep.data.local.entity.DoctorEntity
import com.example.medicalrep.data.local.entity.DraftVisitEntity
import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.entity.UserInventoryEntity
import com.example.medicalrep.data.local.entity.SyncInfoEntity
import com.example.medicalrep.data.local.entity.TrackPointEntity
import com.example.medicalrep.data.local.entity.RepEntity
import com.example.medicalrep.data.local.entity.VisitDoctorEntity
import com.example.medicalrep.data.local.entity.VisitItemEntity
import com.example.medicalrep.data.local.entity.VisitEntity

@Database(
    entities = [
        RepEntity::class,
        CityEntity::class,
        DistrictEntity::class,
        UserDistrictEntity::class,
        AccountEntity::class,
        DoctorEntity::class,
        AccountDoctorEntity::class,
        CatalogItemEntity::class,
        UserInventoryEntity::class,
        VisitEntity::class,
        VisitItemEntity::class,
        VisitDoctorEntity::class,
        DraftVisitEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        TrackPointEntity::class,
        SyncInfoEntity::class,
        RepRequestEntity::class,
        NotificationEntity::class,
        SystemSettingEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repDao(): RepDao
    abstract fun geoDao(): GeoDao
    abstract fun accountDao(): AccountDao
    abstract fun doctorDao(): DoctorDao
    abstract fun catalogDao(): CatalogDao
    abstract fun visitDao(): VisitDao
    abstract fun orderDao(): OrderDao
    abstract fun draftVisitDao(): DraftVisitDao
    abstract fun trackPointDao(): TrackPointDao
    abstract fun syncInfoDao(): SyncInfoDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun requestDao(): RepRequestDao
    abstract fun notificationDao(): NotificationDao
    abstract fun systemSettingDao(): SystemSettingDao
}
