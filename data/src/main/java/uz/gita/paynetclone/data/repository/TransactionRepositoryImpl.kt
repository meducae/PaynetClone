package uz.gita.paynetclone.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.gita.paynetclone.data.remote.api.UserApi
import uz.gita.paynetclone.data.remote.dto.transaction.toEntity
import uz.gita.paynetclone.entity.transaction.TransactionResult
import uz.gita.paynetclone.usecase.transaction.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val api: UserApi
) : TransactionRepository {
    override fun getTransactions(
        page: Int,
        pageSize: Int,
        cardId: String?,
        type: String?
    ): Flow<Result<TransactionResult>> = flow {
        try {
            val response = api.getTransactions(page, pageSize, cardId, type)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Result.success(response.body()!!.toEntity()))
            } else {
                emit(Result.failure(Exception(response.body()?.error?.message ?: response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
