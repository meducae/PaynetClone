package uz.gita.paynetclone.usecase.auth

import javax.inject.Inject

class SendOtpUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend operator fun invoke(phone: String): Result<Unit> {
        if (phone.isBlank()) return Result.failure(Exception("Phone cannot be empty"))

        val digitsOnly = phone.filter { it.isDigit() }
        if (digitsOnly.length < 9) return Result.failure(Exception("Invalid phone format"))

        val formattedPhone = when {
            phone.startsWith("+998") -> phone
            phone.startsWith("998") -> "+$phone"
            phone.startsWith("+") -> phone
            else -> "+998$digitsOnly"
        }

        return repository.sendOtp(formattedPhone)
    }
}
