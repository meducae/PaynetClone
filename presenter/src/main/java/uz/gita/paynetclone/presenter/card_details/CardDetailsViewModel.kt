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

import uz.gita.paynetclone.usecase.user.GetProfileUseCase
import uz.gita.paynetclone.usecase.card.GetCardsUseCase
import uz.gita.paynetclone.usecase.card.DeleteCardUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val deleteCardUseCase: DeleteCardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CardDetailsContract.State())
    val state: StateFlow<CardDetailsContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CardDetailsContract.SideEffect>()
    val sideEffect: SharedFlow<CardDetailsContract.SideEffect> = _sideEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            getProfileUseCase().onSuccess { user ->
                _state.update { it.copy(isKycVerified = user.isKycVerified) }
            }
        }
    }

    fun onEvent(intent: CardDetailsContract.Intent) {
        when (intent) {
            is CardDetailsContract.Intent.LoadCard -> {
                getCardsUseCase().onEach { result ->
                    result.onSuccess { cards ->
                        val card = cards.find { it.id == intent.cardId }
                        if (card != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    card = card,
                                    isMainCard = card.isMain,
                                    remainingTransfers = 1,
                                    maxTransfers = 1
                                )
                            }
                        }
                    }
                }.launchIn(viewModelScope)
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
            CardDetailsContract.Intent.OnDeleteCardClicked -> {
                val cardId = _state.value.card?.id ?: return
                _state.update { it.copy(isLoading = true) }
                deleteCardUseCase(cardId).onEach { result ->
                    _state.update { it.copy(isLoading = false) }
                    result.onSuccess {
                        _sideEffect.emit(CardDetailsContract.SideEffect.ShowToast("Karta muvaffaqiyatli o'chirildi"))
                        _sideEffect.emit(CardDetailsContract.SideEffect.Back)
                    }.onFailure { err ->
                        _sideEffect.emit(CardDetailsContract.SideEffect.ShowToast(err.message ?: "Xatolik yuz berdi"))
                    }
                }.launchIn(viewModelScope)
            }
            else -> {}
        }
    }
}
