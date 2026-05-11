package uz.gita.paynetclone.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.paynetclone.data.repository.TransferRepositoryImpl
import uz.gita.paynetclone.usecase.transfer.TransferRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TransferDataModule {

    @Binds
    @Singleton
    fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository
}
