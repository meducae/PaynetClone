package uz.gita.paynetclone.entity.user

data class User(
    val id: String,
    val phone: String,
    val fullName: String?,
    val isKycVerified: Boolean,
    val createdAt: String
)
