package uz.gita.paynetclone.usecase.payment

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.payment.Provider

interface PaymentRepository {
    fun getProviders(category: String?): Flow<Result<List<Provider>>>
    fun makePayment(
        providerId: String,
        cardId: String,
        amount: Long,
        account: String
    ): Flow<Result<Unit>>
}
