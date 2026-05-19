package uz.gita.paynetclone.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uz.gita.paynetclone.data.remote.dto.auth.SendOtpRequest
import uz.gita.paynetclone.data.remote.dto.auth.SendOtpResponse
import uz.gita.paynetclone.data.remote.dto.auth.SetPinRequest
import uz.gita.paynetclone.data.remote.dto.auth.SetPinResponse
import uz.gita.paynetclone.data.remote.dto.auth.VerifyOtpRequest
import uz.gita.paynetclone.data.remote.dto.auth.VerifyOtpResponse

interface AuthApi {
    @POST("api/v1/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("api/v1/auth/set-pin")
    suspend fun setPin(@Body request: SetPinRequest): Response<SetPinResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>
}
