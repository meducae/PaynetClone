package uz.gita.paynetclone.entity.transfer

data class Transfer(
    val requiresConfirmation: Boolean,
    val confirmToken: String?,
    val status: String
)

data class RecipientCard(
    val cardNumber: String,
    val ownerName: String
)
