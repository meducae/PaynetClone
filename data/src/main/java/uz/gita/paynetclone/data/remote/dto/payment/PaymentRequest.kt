package uz.gita.paynetclone.data.remote.dto.payment

data class PaymentRequest(
    val providerId: String,
    val cardId: String,
    val amount: Long,
    val account: String
)

data class PaymentResponse(
    val success: Boolean,
    val error: PaymentErrorDto? = null
)

data class PaymentErrorDto(
    val code: String?,
    val message: String?
)
