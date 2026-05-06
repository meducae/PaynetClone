package uz.gita.paynetclone.usecase.auth

import javax.inject.Inject

class SetPinUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(pin: String): Result<Unit> = repository.setPin(pin)
}
