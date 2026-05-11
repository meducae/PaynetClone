package uz.gita.paynetclone.data.mapper

import uz.gita.paynetclone.data.remote.dto.transfer.CheckCardData
import uz.gita.paynetclone.data.remote.dto.transfer.TransferData
import uz.gita.paynetclone.entity.transfer.RecipientCard
import uz.gita.paynetclone.entity.transfer.Transfer

fun CheckCardData.toEntity(): RecipientCard = RecipientCard(
    cardNumber = cardNumber,
    ownerName = ownerName
)

fun TransferData.toEntity(): Transfer = Transfer(
    requiresConfirmation = requiresConfirmation,
    confirmToken = confirmToken,
    status = status
)
