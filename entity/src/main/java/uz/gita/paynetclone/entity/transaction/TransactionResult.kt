package uz.gita.paynetclone.entity.transaction

data class TransactionResult(
    val data: List<Transaction>,
    val meta: TransactionMeta
)
