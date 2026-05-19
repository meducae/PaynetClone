package uz.gita.paynetclone.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.paynetclone.data.repository.KycRepositoryImpl
import uz.gita.paynetclone.usecase.kyc.KycRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class KycDataModule {
    @Binds
    abstract fun bindKycRepository(
        impl: KycRepositoryImpl
    ): KycRepository
}
