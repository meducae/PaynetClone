package uz.gita.paynetclone.entity.transaction

data class Transaction(
    val id: String,
    val type: String,
    val amount: Long,
    val currency: String,
    val description: String,
    val status: String,
    val cardId: String,
    val createdAt: String
)
