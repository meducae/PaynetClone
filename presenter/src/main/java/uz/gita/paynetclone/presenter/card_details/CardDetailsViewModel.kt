package uz.gita.paynetclone.presenter.card_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.gita.paynetclone.entity.card.Card
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CardDetailsContract.State())
    val state: StateFlow<CardDetailsContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CardDetailsContract.SideEffect>()
    val sideEffect: SharedFlow<CardDetailsContract.SideEffect> = _sideEffect.asSharedFlow()

    fun onEvent(intent: CardDetailsContract.Intent) {
        when (intent) {
            is CardDetailsContract.Intent.LoadCard -> {
                // Mocking card load
                _state.update {
                    it.copy(
                        isLoading = false,
                        card = Card(
                            id = intent.cardId,
                            maskedNumber = "7777013723143540",
                            holderName = "Owner",
                            expiry = "12/25",
                            balance = 0,
                            currency = "so'm",
                            isMain = true,
                            isBlocked = false,
                            type = "PAYNET"
                        ),
                        isMainCard = true,
                        remainingTransfers = 1,
                        maxTransfers = 1
                    )
                }
            }
            CardDetailsContract.Intent.OnBackClicked -> {
                viewModelScope.launch { _sideEffect.emit(CardDetailsContract.SideEffect.Back) }
            }
            CardDetailsContract.Intent.OnCopyClicked -> {
                viewModelScope.launch { 
                    _sideEffect.emit(CardDetailsContract.SideEffect.ShowToast("Vaqtincha xotiraga nusxalandi")) 
                }
            }
            is CardDetailsContract.Intent.OnMainCardChanged -> {
                _state.update { it.copy(isMainCard = intent.isMain) }
            }
            CardDetailsContract.Intent.OnPayClicked -> {
                 viewModelScope.launch { _sideEffect.emit(CardDetailsContract.SideEffect.NavigateToPay) }
            }
            CardDetailsContract.Intent.OnTopUpClicked -> {
                viewModelScope.launch { _sideEffect.emit(CardDetailsContract.SideEffect.NavigateToTopUp) }
            }
            CardDetailsContract.Intent.OnTransferClicked -> {
                viewModelScope.launch { _sideEffect.emit(CardDetailsContract.SideEffect.NavigateToTransfer) }
            }
            CardDetailsContract.Intent.OnVerifyClicked -> {
                viewModelScope.launch { _sideEffect.emit(CardDetailsContract.SideEffect.NavigateToVerify) }
            }
            else -> {}
        }
    }
}
