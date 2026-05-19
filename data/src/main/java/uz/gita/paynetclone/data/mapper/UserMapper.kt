package uz.gita.paynetclone.data.mapper

import uz.gita.paynetclone.data.remote.dto.user.UserDto
import uz.gita.paynetclone.entity.user.User

fun UserDto.toEntity(): User = User(
    id = id,
    phone = phone,
    fullName = fullName,
    isKycVerified = isKycVerified,
    createdAt = createdAt
)
