package uz.gita.paynetclone.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.paynetclone.data.repository.CardRepositoryImpl
import uz.gita.paynetclone.usecase.card.CardRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CardDataModule {

    @Binds
    @Singleton
    fun bindCardRepository(impl: CardRepositoryImpl): CardRepository
}
