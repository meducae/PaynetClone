package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import uz.gita.paynetclone.data.remote.dto.payment.PaymentRequest
import uz.gita.paynetclone.data.remote.dto.payment.PaymentResponse
import uz.gita.paynetclone.data.remote.dto.payment.ProviderResponse

interface PaymentApi {
    @GET("api/v1/payments/providers")
    suspend fun getProviders(
        @Query("category") category: String? = null
    ): Response<ProviderResponse>

    @POST("api/v1/payments")
    suspend fun makePayment(
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}
