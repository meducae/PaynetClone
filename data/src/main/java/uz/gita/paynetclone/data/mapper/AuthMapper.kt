package uz.gita.paynetclone.data.mapper

import uz.gita.paynetclone.data.remote.dto.auth.AuthDataDto
import uz.gita.paynetclone.entity.auth.AuthResult

fun AuthDataDto.toEntity(): AuthResult = AuthResult(
    accessToken = accessToken,
    refreshToken = refreshToken,
    isNewUser = isNewUser
)
