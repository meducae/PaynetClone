package uz.gita.paynetclone.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("phone") val phone: String
)

data class SendOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SendOtpData?
)

data class SendOtpData(
    @SerializedName("message") val message: String
)

data class VerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)

data class VerifyOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: AuthDataDto?,
    @SerializedName("error") val error: ErrorDto? = null
)

data class ErrorDto(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String
)

data class AuthDataDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("isNewUser") val isNewUser: Boolean
)

data class SetPinRequest(
    @SerializedName("pin") val pin: String
)

data class SetPinResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
