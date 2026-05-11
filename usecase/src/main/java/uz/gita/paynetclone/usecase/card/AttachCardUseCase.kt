package uz.gita.paynetclone.usecase.card

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.card.Card
import javax.inject.Inject

class AttachCardUseCase @Inject constructor(
    private val repository: CardRepository
) {
    operator fun invoke(cardNumber: String): Flow<Result<Card>> {
        return repository.attachCard(cardNumber)
    }
}
