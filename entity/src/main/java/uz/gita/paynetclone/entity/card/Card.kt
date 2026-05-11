package uz.gita.paynetclone.entity.card

data class Card(
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
