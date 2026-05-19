package uz.gita.paynetclone.usecase.payment

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MakePaymentUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(
        providerId: String,
        cardId: String,
        amount: Long,
        account: String
    ): Flow<Result<Unit>> {
        return repository.makePayment(providerId, cardId, amount, account)
    }
}
