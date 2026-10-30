package com.example.medicalrep.data.di

import android.content.Context
import androidx.room.Room
import com.example.medicalrep.data.local.dao.AccountDao
import com.example.medicalrep.data.local.dao.DoctorDao
import com.example.medicalrep.data.local.dao.GeoDao
import com.example.medicalrep.data.local.dao.CatalogDao
import com.example.medicalrep.data.local.dao.NotificationDao
import com.example.medicalrep.data.local.dao.RepRequestDao
import com.example.medicalrep.data.local.dao.SystemSettingDao
import com.example.medicalrep.data.local.dao.DraftVisitDao
import com.example.medicalrep.data.local.dao.OrderDao
import com.example.medicalrep.data.local.dao.StatisticsDao
import com.example.medicalrep.data.local.dao.SyncInfoDao
import com.example.medicalrep.data.local.dao.TrackPointDao
import com.example.medicalrep.data.local.dao.RepDao
import com.example.medicalrep.data.local.dao.VisitDao
import com.example.medicalrep.data.local.db.AppDatabase
import com.example.medicalrep.data.local.db.DatabaseOwner
import com.example.medicalrep.data.local.media.MediaFileStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "medicalrep.db")
            .build()

    @Provides
    @Singleton
    fun provideDatabaseOwner(
        @ApplicationContext context: Context,
        database: AppDatabase,
        media: MediaFileStorage,
    ): DatabaseOwner {
        val prefs = context.getSharedPreferences("database_owner", Context.MODE_PRIVATE)
        return DatabaseOwner(
            readOwnerId = { prefs.getString("userId", null) },
            writeOwnerId = { prefs.edit().putString("userId", it).apply() },
            wipe = {
                withContext(Dispatchers.IO) {
                    database.clearAllTables()
                    media.deleteAll()
                }
            },
        )
    }

    @Provides fun provideRepDao(db: AppDatabase): RepDao = db.repDao()
    @Provides fun provideGeoDao(db: AppDatabase): GeoDao = db.geoDao()
    @Provides fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides fun provideDoctorDao(db: AppDatabase): DoctorDao = db.doctorDao()
    @Provides fun provideCatalogDao(db: AppDatabase): CatalogDao = db.catalogDao()
    @Provides fun provideVisitDao(db: AppDatabase): VisitDao = db.visitDao()
    @Provides fun provideDraftVisitDao(db: AppDatabase): DraftVisitDao = db.draftVisitDao()
    @Provides fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
    @Provides fun provideTrackPointDao(db: AppDatabase): TrackPointDao = db.trackPointDao()
    @Provides fun provideSyncInfoDao(db: AppDatabase): SyncInfoDao = db.syncInfoDao()
    @Provides fun provideStatisticsDao(db: AppDatabase): StatisticsDao = db.statisticsDao()
    @Provides fun provideRepRequestDao(db: AppDatabase): RepRequestDao = db.requestDao()
    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideSystemSettingDao(db: AppDatabase): SystemSettingDao = db.systemSettingDao()
}
