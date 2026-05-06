package uz.gita.paynetclone.usecase.auth

import uz.gita.paynetclone.entity.auth.AuthResult
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(phone: String, otp: String): Result<AuthResult> {
        if (otp.isBlank()) return Result.failure(Exception("OTP cannot be empty"))
        if (otp.length != 6) return Result.failure(Exception("OTP must be 6 digits"))

        val digitsOnly = phone.filter { it.isDigit() }
        val formattedPhone = when {
            phone.startsWith("+998") -> phone
            phone.startsWith("998") -> "+$phone"
            phone.startsWith("+") -> phone
            else -> "+998$digitsOnly"
        }

        return repository.verifyOtp(formattedPhone, otp)
    }
}
