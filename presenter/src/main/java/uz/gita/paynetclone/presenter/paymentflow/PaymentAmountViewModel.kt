package uz.gita.paynetclone.presenter.paymentflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.usecase.card.GetCardsUseCase
import javax.inject.Inject

@HiltViewModel
class PaymentAmountViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase
) : ViewModel() {
    
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        getCardsUseCase().onEach { result ->
            result.onSuccess { list ->
                _cards.update { list }
                if (list.isNotEmpty() && _selectedCard.value == null) {
                    _selectedCard.update { list.first() }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun selectCard(card: Card) {
        _selectedCard.update { card }
    }
}
