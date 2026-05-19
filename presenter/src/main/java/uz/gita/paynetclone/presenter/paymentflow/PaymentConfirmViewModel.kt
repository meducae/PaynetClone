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
import uz.gita.paynetclone.usecase.payment.MakePaymentUseCase
import javax.inject.Inject

@HiltViewModel
class PaymentConfirmViewModel @Inject constructor(
    private val makePaymentUseCase: MakePaymentUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )

    fun makePayment(providerId: String, cardId: String, amount: Long, account: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        makePaymentUseCase(providerId, cardId, amount, account).onEach { result ->
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }.launchIn(viewModelScope)
    }
}
