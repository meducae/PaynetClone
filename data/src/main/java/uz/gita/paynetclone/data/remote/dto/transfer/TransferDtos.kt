package uz.gita.paynetclone.data.remote.dto.transfer

import uz.gita.paynetclone.data.remote.dto.card.CardErrorDto

data class TransferRequest(
    val fromCardId: String,
    val toCardNumber: String,
    val amount: Long,
    val pin: String,
    val description: String? = null
)

data class TransferResponse(
    val success: Boolean,
    val data: TransferData? = null,
    val error: CardErrorDto? = null
)

data class TransferData(
    val requiresConfirmation: Boolean,
    val confirmToken: String?,
    val status: String
)

data class CheckCardResponse(
    val success: Boolean,
    val data: CheckCardData? = null,
    val error: CardErrorDto? = null
)

data class CheckCardData(
    val cardNumber: String,
    val ownerName: String
)

data class ConfirmPinRequest(
    val confirmToken: String,
    val pin: String
)

data class ConfirmOtpRequest(
    val confirmToken: String,
    val code: String
)
