package uz.gita.paynetclone.presenter.transfers

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
import uz.gita.paynetclone.usecase.card.CardRepository
import uz.gita.paynetclone.usecase.transfer.TransferRepository
import javax.inject.Inject

@HiltViewModel
class TransfersViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val transferRepository: TransferRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransfersContract.State())
    val state: StateFlow<TransfersContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TransfersContract.SideEffect>()
    val sideEffect: SharedFlow<TransfersContract.SideEffect> = _sideEffect.asSharedFlow()

    init {
        loadMyCards()
    }

    private fun loadMyCards() {
        viewModelScope.launch {
            cardRepository.getCards().collect { result ->
                result.onSuccess { cards ->
                    _state.update { it.copy(myCards = cards, selectedFromCard = cards.find { it.isMain } ?: cards.firstOrNull()) }
                }.onFailure { /* Handle error */ }
            }
        }
    }

    fun onEvent(event: TransfersContract.Intent) {
        when (event) {
            is TransfersContract.Intent.SearchChanged -> {
                _state.update { it.copy(searchText = event.text) }
                if (event.text.length == 16) {
                    checkRecipient(event.text)
                }
            }

            is TransfersContract.Intent.TabSelected -> {
                _state.update { it.copy(selectedTab = event.index) }
            }

            TransfersContract.Intent.OnSearchClicked -> {
                _state.update { it.copy(isSearching = true) }
            }

            TransfersContract.Intent.OnCancelSearchClicked -> {
                _state.update { it.copy(isSearching = false, searchText = "", searchedRecipient = null) }
            }

            is TransfersContract.Intent.OnRecipientSelected -> {
                _state.update {
                    it.copy(
                        selectedToCard = event.recipient,
                        transferStep = TransfersContract.TransferStep.DETAILS,
                        isSearching = false
                    )
                }
            }

            is TransfersContract.Intent.OnFromCardSelected -> {
                _state.update { it.copy(selectedFromCard = event.card, isCardSelectionVisible = false) }
            }

            TransfersContract.Intent.OpenCardSelection -> {
                _state.update { it.copy(isCardSelectionVisible = true) }
            }

            TransfersContract.Intent.DismissCardSelection -> {
                _state.update { it.copy(isCardSelectionVisible = false) }
            }

            is TransfersContract.Intent.AmountChanged -> {
                _state.update { it.copy(amount = event.amount) }
            }

            TransfersContract.Intent.OnContinueClicked -> {
                _state.update { it.copy(transferStep = TransfersContract.TransferStep.CONFIRMATION) }
            }

            TransfersContract.Intent.OnConfirmTransferClicked -> {
                initiateTransfer()
            }

            is TransfersContract.Intent.OnPinEntered -> {
                confirmPin(event.pin)
            }

            is TransfersContract.Intent.OnOtpEntered -> {
                confirmOtp(event.code)
            }

            TransfersContract.Intent.OnBackClicked -> {
                when (_state.value.transferStep) {
                    TransfersContract.TransferStep.DETAILS -> {
                        _state.update { it.copy(transferStep = TransfersContract.TransferStep.SEARCH, isSearching = true) }
                    }

                    TransfersContract.TransferStep.CONFIRMATION -> {
                        _state.update { it.copy(transferStep = TransfersContract.TransferStep.DETAILS) }
                    }

                    TransfersContract.TransferStep.SUCCESS -> {
                        _state.update { it.copy(transferStep = TransfersContract.TransferStep.SEARCH, amount = "", searchText = "") }
                    }

                    else -> {
                        _state.update { it.copy(transferStep = TransfersContract.TransferStep.SEARCH) }
                    }
                }
            }

            TransfersContract.Intent.OnContactClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToContacts) }
            }

            TransfersContract.Intent.OnQrClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToQrScanner) }
            }

            TransfersContract.Intent.OnUzumBannerClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToUzumTransfer) }
            }

            TransfersContract.Intent.OnIdentificationClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToIdentification) }
            }

            TransfersContract.Intent.OnMyCardsClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToMyCards) }
            }

            TransfersContract.Intent.OnAttoClicked -> {
                viewModelScope.launch { _sideEffect.emit(TransfersContract.SideEffect.NavigateToAtto) }
            }
        }
    }

    private fun checkRecipient(cardNumber: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            transferRepository.checkCard(cardNumber).collect { result ->
                _state.update { it.copy(isLoading = false) }
                result.onSuccess { recipient ->
                    _state.update { it.copy(searchedRecipient = recipient.copy(cardNumber = cardNumber)) }
                }.onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    private fun initiateTransfer() {
        val currentState = _state.value
        val fromCard = currentState.selectedFromCard ?: return
        val toCard = currentState.selectedToCard ?: return
        val amount = currentState.amount.toLongOrNull() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            transferRepository.initiateTransfer(
                fromCardId = fromCard.id,
                toCardNumber = toCard.cardNumber,
                amount = amount,
                pin = ""
            ).collect { result ->
                _state.update { it.copy(isLoading = false) }
                result.onSuccess { transfer ->
                    if (transfer.requiresConfirmation) {
                        val nextStep = if (transfer.status == "WAITING_OTP") {
                            TransfersContract.TransferStep.CONFIRM_OTP
                        } else {
                            TransfersContract.TransferStep.CONFIRM_PIN
                        }
                        _state.update {
                            it.copy(
                                transferStep = nextStep,
                                confirmToken = transfer.confirmToken
                            )
                        }
                    } else {
                        _state.update { it.copy(transferStep = TransfersContract.TransferStep.SUCCESS) }
                    }
                }.onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    private fun confirmPin(pin: String) {
        val token = _state.value.confirmToken ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            transferRepository.confirmPin(token, pin).collect { result ->
                _state.update { it.copy(isLoading = false) }
                result.onSuccess {
                    _state.update { it.copy(transferStep = TransfersContract.TransferStep.SUCCESS) }
                }.onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    private fun confirmOtp(code: String) {
        val token = _state.value.confirmToken ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            transferRepository.confirmOtp(token, code).collect { result ->
                _state.update { it.copy(isLoading = false) }
                result.onSuccess {
                    _state.update { it.copy(transferStep = TransfersContract.TransferStep.SUCCESS) }
                }.onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }
}
