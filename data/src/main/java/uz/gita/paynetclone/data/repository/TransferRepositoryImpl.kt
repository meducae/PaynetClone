package uz.gita.paynetclone.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import uz.gita.paynetclone.data.mapper.toEntity
import uz.gita.paynetclone.data.remote.api.TransferApi
import uz.gita.paynetclone.data.remote.dto.transfer.*
import uz.gita.paynetclone.entity.transfer.RecipientCard
import uz.gita.paynetclone.entity.transfer.Transfer
import uz.gita.paynetclone.usecase.transfer.TransferRepository
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val transferApi: TransferApi
) : TransferRepository {

    override fun checkCard(cardNumber: String): Flow<Result<RecipientCard>> = flow {
        try {
            val response = transferApi.checkCard(cardNumber)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                emit(Result.success(body.data!!.toEntity()))
            } else {
                emit(Result.failure(Exception(parseError(response.errorBody(), body?.error))))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun initiateTransfer(
        fromCardId: String,
        toCardNumber: String,
        amount: Long,
        pin: String,
        description: String?
    ): Flow<Result<Transfer>> = flow {
        try {
            val request = TransferRequest(fromCardId, toCardNumber, amount, pin, description)
            val response = transferApi.initiateTransfer(request)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                emit(Result.success(body.data!!.toEntity()))
            } else {
                emit(Result.failure(Exception(parseError(response.errorBody(), body?.error))))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun confirmPin(token: String, pin: String): Flow<Result<Transfer>> = flow {
        try {
            val response = transferApi.confirmPin(ConfirmPinRequest(token, pin))
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                emit(Result.success(body.data!!.toEntity()))
            } else {
                emit(Result.failure(Exception(parseError(response.errorBody(), body?.error))))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun confirmOtp(token: String, code: String): Flow<Result<Transfer>> = flow {
        try {
            val response = transferApi.confirmOtp(ConfirmOtpRequest(token, code))
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                emit(Result.success(body.data!!.toEntity()))
            } else {
                emit(Result.failure(Exception(parseError(response.errorBody(), body?.error))))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun parseError(errorBody: ResponseBody?, errorDto: uz.gita.paynetclone.data.remote.dto.card.CardErrorDto?): String {
        if (errorDto != null) return errorDto.message
        return try {
            val raw = errorBody?.string() ?: return "Unknown error"
            val json = Gson().fromJson(raw, JsonObject::class.java)
            json?.getAsJsonObject("error")?.get("message")?.asString
                ?: json?.get("message")?.asString
                ?: raw
        } catch (_: Exception) {
            "Unknown error"
        }
    }
}
