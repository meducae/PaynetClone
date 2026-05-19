package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uz.gita.paynetclone.data.remote.dto.transaction.TransactionResponse
import uz.gita.paynetclone.data.remote.dto.user.UserProfileResponse

interface UserApi {
    @GET("api/v1/users/me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("api/v1/users/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("cardId") cardId: String? = null,
        @Query("type") type: String? = null
    ): Response<TransactionResponse>
}
