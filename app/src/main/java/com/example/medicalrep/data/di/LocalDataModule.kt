package com.example.medicalrep.data.di

import com.example.medicalrep.data.repo.AccountRepoImpl
import com.example.medicalrep.data.repo.DoctorRepoImpl
import com.example.medicalrep.data.repo.GeoRepoImpl
import com.example.medicalrep.data.repo.DraftVisitRepoImpl
import com.example.medicalrep.data.repo.OrderRepoImpl
import com.example.medicalrep.data.repo.NotificationRepoImpl
import com.example.medicalrep.data.repo.ProductRepoImpl
import com.example.medicalrep.data.repo.RequestRepoImpl
import com.example.medicalrep.data.repo.SystemSettingRepoImpl
import com.example.medicalrep.data.repo.StatisticsRepoImpl
import com.example.medicalrep.data.repo.SyncInfoRepoImpl
import com.example.medicalrep.data.repo.TrackPointRepoImpl
import com.example.medicalrep.data.repo.RepRepoImpl
import com.example.medicalrep.data.repo.VisitRepoImpl
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.domain.repo.DoctorRepo
import com.example.medicalrep.domain.repo.GeoRepo
import com.example.medicalrep.domain.repo.DraftVisitRepo
import com.example.medicalrep.domain.repo.OrderRepo
import com.example.medicalrep.domain.repo.NotificationRepo
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.domain.repo.RequestRepo
import com.example.medicalrep.domain.repo.SystemSettingRepo
import com.example.medicalrep.domain.repo.StatisticsRepo
import com.example.medicalrep.domain.repo.SyncInfoRepo
import com.example.medicalrep.domain.repo.TrackPointRepo
import com.example.medicalrep.domain.repo.RepRepo
import com.example.medicalrep.domain.repo.VisitRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {
    @Binds abstract fun bindVisitRepo(impl: VisitRepoImpl): VisitRepo
    @Binds abstract fun bindDraftVisitRepo(impl: DraftVisitRepoImpl): DraftVisitRepo
    @Binds abstract fun bindAccountRepo(impl: AccountRepoImpl): AccountRepo
    @Binds abstract fun bindGeoRepo(impl: GeoRepoImpl): GeoRepo
    @Binds abstract fun bindDoctorRepo(impl: DoctorRepoImpl): DoctorRepo
    @Binds abstract fun bindProductRepo(impl: ProductRepoImpl): ProductRepo
    @Binds abstract fun bindOrderRepo(impl: OrderRepoImpl): OrderRepo
    @Binds abstract fun bindRepRepo(impl: RepRepoImpl): RepRepo
    @Binds abstract fun bindTrackPointRepo(impl: TrackPointRepoImpl): TrackPointRepo
    @Binds abstract fun bindSyncInfoRepo(impl: SyncInfoRepoImpl): SyncInfoRepo
    @Binds abstract fun bindStatisticsRepo(impl: StatisticsRepoImpl): StatisticsRepo
    @Binds abstract fun bindRequestRepo(impl: RequestRepoImpl): RequestRepo
    @Binds abstract fun bindNotificationRepo(impl: NotificationRepoImpl): NotificationRepo
    @Binds abstract fun bindSystemSettingRepo(impl: SystemSettingRepoImpl): SystemSettingRepo
}
