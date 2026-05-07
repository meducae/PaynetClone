package uz.gita.paynetclone.usecase.user

import uz.gita.paynetclone.entity.user.User
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = repository.getProfile()
}
