package uz.gita.paynetclone.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserDto?
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("isKycVerified") val isKycVerified: Boolean,
    @SerializedName("createdAt") val createdAt: String
)
