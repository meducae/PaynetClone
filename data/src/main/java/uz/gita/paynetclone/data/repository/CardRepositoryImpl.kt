package uz.gita.paynetclone.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import uz.gita.paynetclone.data.mapper.toEntity
import uz.gita.paynetclone.data.remote.api.CardApi
import uz.gita.paynetclone.data.remote.dto.card.AttachCardRequest
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.usecase.card.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardApi: CardApi
) : CardRepository {

    override fun attachCard(cardNumber: String): Flow<Result<Card>> = flow {
        try {
            val response = cardApi.attachCard(AttachCardRequest(cardNumber))
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                val cardDto = body.data
                if (cardDto != null) {
                    emit(Result.success(cardDto.toEntity()))
                } else {
                    emit(Result.failure(Exception("Empty card data in response")))
                }
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                    ?: body?.error?.message
                    ?: "HTTP ${response.code()}: ${response.message()}"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCards(): Flow<Result<List<Card>>> = flow {
        try {
            val response = cardApi.getCards()
            val body = response.body()

            if (response.isSuccessful && body != null && body.success) {
                val cardDtos = body.data
                if (cardDtos != null) {
                    emit(Result.success(cardDtos.map { it.toEntity() }))
                } else {
                    emit(Result.success(emptyList()))
                }
            } else {
                val errorMessage = parseErrorBody(response.errorBody())
                    ?: body?.error?.message
                    ?: "HTTP ${response.code()}: ${response.message()}"
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody?): String? {
        return try {
            val raw = errorBody?.string() ?: return null
            val json = Gson().fromJson(raw, JsonObject::class.java)

            json?.getAsJsonObject("error")?.get("message")?.asString
                ?: json?.get("message")?.asString
                ?: json?.get("error")?.asString
                ?: if (raw.length < 100) raw else null
        } catch (_: Exception) {
            null
        }
    }
}
