package uz.gita.paynetclone.presenter.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.paynetclone.presenter.navigation.AppNavigator
import uz.gita.paynetclone.usecase.card.CardRepository
import javax.inject.Inject

@HiltViewModel
class MyCardsViewModel @Inject constructor(
    private val repository: CardRepository,
    private val navigator: AppNavigator
) : ViewModel(), MyCardsContract.ViewModel {

    private val _uiState = MutableStateFlow(MyCardsContract.UiState())
    override val uiState: StateFlow<MyCardsContract.UiState> = _uiState.asStateFlow()

    init {
        onEventDispatcher(MyCardsContract.Intent.LoadCards)
    }

    override fun onEventDispatcher(intent: MyCardsContract.Intent) {
        when (intent) {
            MyCardsContract.Intent.LoadCards -> {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getCards()
                    .onEach { result ->
                        result.onSuccess { cards ->
                            _uiState.value = _uiState.value.copy(cards = cards, isLoading = false)
                        }.onFailure {
                            _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
                        }
                    }.launchIn(viewModelScope)
            }
            MyCardsContract.Intent.AddCard -> {
                navigator.openAddCard()
            }
            MyCardsContract.Intent.Back -> {
                navigator.back()
            }
            is MyCardsContract.Intent.CopyCardNumber -> {
                // Handled in UI usually or via a side effect
            }
            is MyCardsContract.Intent.OpenDetails -> {
                navigator.openCardDetails(intent.cardId)
            }
        }
    }
}
