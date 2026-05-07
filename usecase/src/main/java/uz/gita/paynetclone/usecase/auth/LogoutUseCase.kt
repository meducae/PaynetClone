package uz.gita.paynetclone.usecase.auth

import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
