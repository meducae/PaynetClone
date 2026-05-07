package uz.gita.paynetclone.usecase.user

import uz.gita.paynetclone.entity.user.User

interface UserRepository {
    suspend fun getProfile(): Result<User>
}
