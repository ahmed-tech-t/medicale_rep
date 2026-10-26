package com.example.medicalrep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicalrep.data.local.dao.AccountDao
import com.example.medicalrep.data.local.dao.CustodyDao
import com.example.medicalrep.data.local.dao.DraftVisitDao
import com.example.medicalrep.data.local.dao.OrderDao
import com.example.medicalrep.data.local.dao.ProductDao
import com.example.medicalrep.data.local.dao.SyncInfoDao
import com.example.medicalrep.data.local.dao.TrackPointDao
import com.example.medicalrep.data.local.dao.UserDao
import com.example.medicalrep.data.local.dao.VisitDao
import com.example.medicalrep.data.local.entity.AccountEntity
import com.example.medicalrep.data.local.entity.CustodyEntity
import com.example.medicalrep.data.local.entity.DraftVisitEntity
import com.example.medicalrep.data.local.entity.OrderEntity
import com.example.medicalrep.data.local.entity.OrderItemEntity
import com.example.medicalrep.data.local.entity.ProductEntity
import com.example.medicalrep.data.local.entity.SyncInfoEntity
import com.example.medicalrep.data.local.entity.TrackPointEntity
import com.example.medicalrep.data.local.entity.UserEntity
import com.example.medicalrep.data.local.entity.VisitCustodyEntity
import com.example.medicalrep.data.local.entity.VisitEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        ProductEntity::class,
        CustodyEntity::class,
        VisitEntity::class,
        VisitCustodyEntity::class,
        DraftVisitEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        TrackPointEntity::class,
        SyncInfoEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun productDao(): ProductDao
    abstract fun custodyDao(): CustodyDao
    abstract fun visitDao(): VisitDao
    abstract fun orderDao(): OrderDao
    abstract fun draftVisitDao(): DraftVisitDao
    abstract fun trackPointDao(): TrackPointDao
    abstract fun syncInfoDao(): SyncInfoDao
}
