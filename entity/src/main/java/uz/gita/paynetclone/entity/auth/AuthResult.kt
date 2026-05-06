package uz.gita.paynetclone.entity.auth

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean
)
