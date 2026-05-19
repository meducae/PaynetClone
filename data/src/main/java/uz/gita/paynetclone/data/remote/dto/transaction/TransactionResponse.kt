package uz.gita.paynetclone.data.remote.dto.transaction

import uz.gita.paynetclone.entity.transaction.Transaction
import uz.gita.paynetclone.entity.transaction.TransactionMeta
import uz.gita.paynetclone.entity.transaction.TransactionResult

data class TransactionResponse(
    val success: Boolean,
    val data: List<TransactionDto>? = null,
    val meta: TransactionMetaDto? = null,
    val error: ErrorDto? = null
)

data class TransactionDto(
    val id: String?,
    val type: String?,
    val amount: Long?,
    val currency: String?,
    val description: String?,
    val status: String?,
    val cardId: String?,
    val createdAt: String?
) {
    fun toEntity() = Transaction(
        id = id ?: "",
        type = type ?: "",
        amount = amount ?: 0L,
        currency = currency ?: "UZS",
        description = description ?: "",
        status = status ?: "",
        cardId = cardId ?: "",
        createdAt = createdAt ?: ""
    )
}

data class TransactionMetaDto(
    val page: Int?,
    val pageSize: Int?,
    val total: Int?,
    val totalPages: Int?
) {
    fun toEntity() = TransactionMeta(
        page = page ?: 1,
        pageSize = pageSize ?: 20,
        total = total ?: 0,
        totalPages = totalPages ?: 0
    )
}

data class ErrorDto(
    val code: String?,
    val message: String?
)

fun TransactionResponse.toEntity(): TransactionResult {
    return TransactionResult(
        data = this.data?.map { it.toEntity() } ?: emptyList(),
        meta = this.meta?.toEntity() ?: TransactionMeta(1, 20, 0, 0)
    )
}
