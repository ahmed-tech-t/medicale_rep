package com.example.medicalrep.data.di

import android.content.Context
import androidx.room.Room
import com.example.medicalrep.BuildConfig
import com.example.medicalrep.data.local.dao.AccountDao
import com.example.medicalrep.data.local.dao.CustodyDao
import com.example.medicalrep.data.local.dao.DraftVisitDao
import com.example.medicalrep.data.local.dao.OrderDao
import com.example.medicalrep.data.local.dao.ProductDao
import com.example.medicalrep.data.local.dao.StatisticsDao
import com.example.medicalrep.data.local.dao.SyncInfoDao
import com.example.medicalrep.data.local.dao.TrackPointDao
import com.example.medicalrep.data.local.dao.UserDao
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
            // Real migrations from version 2; only debug builds may wipe the data while the tables still change.
            .apply { if (BuildConfig.DEBUG) fallbackToDestructiveMigration(dropAllTables = true) }
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

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    @Provides fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
    @Provides fun provideCustodyDao(db: AppDatabase): CustodyDao = db.custodyDao()
    @Provides fun provideVisitDao(db: AppDatabase): VisitDao = db.visitDao()
    @Provides fun provideDraftVisitDao(db: AppDatabase): DraftVisitDao = db.draftVisitDao()
    @Provides fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
    @Provides fun provideTrackPointDao(db: AppDatabase): TrackPointDao = db.trackPointDao()
    @Provides fun provideSyncInfoDao(db: AppDatabase): SyncInfoDao = db.syncInfoDao()
    @Provides fun provideStatisticsDao(db: AppDatabase): StatisticsDao = db.statisticsDao()
}
