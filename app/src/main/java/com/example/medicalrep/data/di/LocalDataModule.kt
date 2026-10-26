package com.example.medicalrep.data.di

import com.example.medicalrep.data.repo.AccountRepoImpl
import com.example.medicalrep.data.repo.OrderRepoImpl
import com.example.medicalrep.data.repo.ProductRepoImpl
import com.example.medicalrep.data.repo.SyncInfoRepoImpl
import com.example.medicalrep.data.repo.TrackPointRepoImpl
import com.example.medicalrep.data.repo.UserRepoImpl
import com.example.medicalrep.data.repo.VisitRepoImpl
import com.example.medicalrep.domain.repo.AccountRepo
import com.example.medicalrep.domain.repo.OrderRepo
import com.example.medicalrep.domain.repo.ProductRepo
import com.example.medicalrep.domain.repo.SyncInfoRepo
import com.example.medicalrep.domain.repo.TrackPointRepo
import com.example.medicalrep.domain.repo.UserRepo
import com.example.medicalrep.domain.repo.VisitRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {
    @Binds abstract fun bindVisitRepo(impl: VisitRepoImpl): VisitRepo
    @Binds abstract fun bindAccountRepo(impl: AccountRepoImpl): AccountRepo
    @Binds abstract fun bindProductRepo(impl: ProductRepoImpl): ProductRepo
    @Binds abstract fun bindOrderRepo(impl: OrderRepoImpl): OrderRepo
    @Binds abstract fun bindUserRepo(impl: UserRepoImpl): UserRepo
    @Binds abstract fun bindTrackPointRepo(impl: TrackPointRepoImpl): TrackPointRepo
    @Binds abstract fun bindSyncInfoRepo(impl: SyncInfoRepoImpl): SyncInfoRepo
}
