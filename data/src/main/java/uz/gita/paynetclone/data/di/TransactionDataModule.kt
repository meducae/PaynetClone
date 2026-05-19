package uz.gita.paynetclone.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.paynetclone.data.repository.TransactionRepositoryImpl
import uz.gita.paynetclone.usecase.transaction.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TransactionDataModule {
    @Binds
    @Singleton
    fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository
}
