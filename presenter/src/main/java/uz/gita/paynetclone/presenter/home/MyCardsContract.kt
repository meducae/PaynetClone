package uz.gita.paynetclone.presenter.home

import kotlinx.coroutines.flow.StateFlow
import uz.gita.paynetclone.entity.card.Card

interface MyCardsContract {
    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(intent: Intent)
    }

    data class UiState(
        val cards: List<Card> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        object LoadCards : Intent
        object AddCard : Intent
        object Back : Intent
        data class CopyCardNumber(val number: String) : Intent
        data class OpenDetails(val cardId: String) : Intent
    }
}
