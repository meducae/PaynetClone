package uz.gita.paynetclone.usecase.transaction

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.transaction.TransactionResult

interface TransactionRepository {
    fun getTransactions(
        page: Int = 1,
        pageSize: Int = 20,
        cardId: String? = null,
        type: String? = null
    ): Flow<Result<TransactionResult>>
}
