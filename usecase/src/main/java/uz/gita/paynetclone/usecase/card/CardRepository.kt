package uz.gita.paynetclone.usecase.card

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.card.Card

interface CardRepository {
    fun attachCard(cardNumber: String): Flow<Result<Card>>
    fun getCards(): Flow<Result<List<Card>>>
}
