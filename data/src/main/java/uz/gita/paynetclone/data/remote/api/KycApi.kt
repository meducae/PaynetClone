package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import uz.gita.paynetclone.data.remote.dto.kyc.KycStatusResponse
import uz.gita.paynetclone.data.remote.dto.kyc.KycSubmitRequest
import uz.gita.paynetclone.data.remote.dto.kyc.KycSubmitResponse

interface KycApi {
    @GET("api/v1/kyc")
    suspend fun getKycStatus(): Response<KycStatusResponse>

    @POST("api/v1/kyc")
    suspend fun submitKyc(@Body request: KycSubmitRequest): Response<KycSubmitResponse>
}
