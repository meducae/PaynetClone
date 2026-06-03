package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import uz.gita.paynetclone.data.remote.dto.card.AttachCardRequest
import uz.gita.paynetclone.data.remote.dto.card.CardResponse
import uz.gita.paynetclone.data.remote.dto.card.CardsListResponse
import uz.gita.paynetclone.data.remote.dto.card.DetachCardResponse

interface CardApi {
    @POST("api/v1/cards/attach")
    suspend fun attachCard(@Body request: AttachCardRequest): Response<CardResponse>

    @GET("api/v1/cards")
    suspend fun getCards(): Response<CardsListResponse>

    @DELETE("api/v1/cards/{id}")
    suspend fun deleteCard(@Path("id") cardId: String): Response<DetachCardResponse>
}
