package uz.gita.paynetclone.usecase.card

import kotlinx.coroutines.flow.Flow
import uz.gita.paynetclone.entity.card.Card
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val repository: CardRepository
) {
    operator fun invoke(): Flow<Result<List<Card>>> {
        return repository.getCards()
    }
}
