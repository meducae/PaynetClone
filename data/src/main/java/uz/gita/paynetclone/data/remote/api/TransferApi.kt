package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import uz.gita.paynetclone.data.remote.dto.transfer.*

interface TransferApi {
    @POST("api/v1/transfers")
    suspend fun initiateTransfer(@Body request: TransferRequest): Response<TransferResponse>

    @GET("api/v1/transfers/check-card")
    suspend fun checkCard(@Query("cardNumber") cardNumber: String): Response<CheckCardResponse>

    @POST("api/v1/transfers/confirm-pin")
    suspend fun confirmPin(@Body request: ConfirmPinRequest): Response<TransferResponse>

    @POST("api/v1/transfers/confirm-otp")
    suspend fun confirmOtp(@Body request: ConfirmOtpRequest): Response<TransferResponse>
}
