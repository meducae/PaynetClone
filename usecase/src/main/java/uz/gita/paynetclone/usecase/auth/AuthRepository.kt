package uz.gita.paynetclone.usecase.auth

import uz.gita.paynetclone.entity.auth.AuthResult

interface AuthRepository {
    suspend fun sendOtp(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<AuthResult>
    suspend fun setPin(pin: String): Result<Unit>
}
