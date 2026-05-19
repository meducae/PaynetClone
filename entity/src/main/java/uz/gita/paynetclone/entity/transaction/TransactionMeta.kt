package uz.gita.paynetclone.entity.transaction

data class TransactionMeta(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)
