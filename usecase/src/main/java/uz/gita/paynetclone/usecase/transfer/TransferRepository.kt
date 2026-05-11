package uz.gita.paynetclone.usecase.transfer

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.transfer.RecipientCard
import uz.gita.paynetclone.entity.transfer.Transfer

interface TransferRepository {
    fun checkCard(cardNumber: String): Flow<Result<RecipientCard>>
    fun initiateTransfer(
        fromCardId: String,
        toCardNumber: String,
        amount: Long,
        pin: String,
        description: String? = null
    ): Flow<Result<Transfer>>
    fun confirmPin(token: String, pin: String): Flow<Result<Transfer>>
    fun confirmOtp(token: String, code: String): Flow<Result<Transfer>>
}
