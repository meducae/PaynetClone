package uz.gita.paynetclone.data.mapper

import uz.gita.paynetclone.data.remote.dto.card.CardDto
import uz.gita.paynetclone.entity.card.Card

fun CardDto.toEntity(): Card = Card(
    id = id,
    maskedNumber = maskedNumber,
    holderName = holderName,
    expiry = expiry,
    balance = balance,
    currency = currency,
    isMain = isMain,
    isBlocked = isBlocked,
    type = type
)
