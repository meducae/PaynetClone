package uz.gita.paynetclone.data.remote.dto.card

data class AttachCardRequest(
    val cardNumber: String
)

data class CardResponse(
    val success: Boolean,
    val data: CardDto?,
    val error: CardErrorDto?
)

data class CardsListResponse(
    val success: Boolean,
    val data: List<CardDto>?,
    val error: CardErrorDto?
)

data class CardDto(
    val id: String,
    val maskedNumber: String,
    val holderName: String,
    val expiry: String,
    val balance: Long,
    val currency: String,
    val isMain: Boolean,
    val isBlocked: Boolean,
    val type: String
)

data class CardErrorDto(
    val code: String,
    val message: String
)
