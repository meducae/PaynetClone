package uz.gita.paynetclone.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.gita.paynetclone.data.remote.api.PaymentApi
import uz.gita.paynetclone.entity.payment.Provider
import uz.gita.paynetclone.usecase.payment.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: PaymentApi
) : PaymentRepository {
    override fun getProviders(category: String?): Flow<Result<List<Provider>>> = flow {
        try {
            val response = api.getProviders(category)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Result.success(response.body()?.data?.map { it.toEntity() } ?: emptyList()))
            } else {
                emit(Result.failure(Exception("Failed to get providers")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun makePayment(
        providerId: String,
        cardId: String,
        amount: Long,
        account: String
    ): Flow<Result<Unit>> = flow {
        try {
            val request = uz.gita.paynetclone.data.remote.dto.payment.PaymentRequest(
                providerId = providerId,
                cardId = cardId,
                amount = amount,
                account = account
            )
            val response = api.makePayment(request)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Result.success(Unit))
            } else {
                val errorMsg = response.body()?.error?.message ?: response.message()
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
