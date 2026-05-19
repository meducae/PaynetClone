package uz.gita.paynetclone.usecase.transaction

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.transaction.TransactionResult
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        page: Int = 1,
        pageSize: Int = 20,
        cardId: String? = null,
        type: String? = null
    ): Flow<Result<TransactionResult>> {
        return repository.getTransactions(page, pageSize, cardId, type)
    }
}
