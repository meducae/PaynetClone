package uz.gita.paynetclone.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uz.gita.paynetclone.data.remote.api.PaymentApi
import uz.gita.paynetclone.data.repository.PaymentRepositoryImpl
import uz.gita.paynetclone.usecase.payment.PaymentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PaymentDataModule {
    @Binds
    @Singleton
    fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    companion object {
        @Provides
        @Singleton
        fun providePaymentApi(retrofit: Retrofit): PaymentApi {
            return retrofit.create(PaymentApi::class.java)
        }
    }
}
