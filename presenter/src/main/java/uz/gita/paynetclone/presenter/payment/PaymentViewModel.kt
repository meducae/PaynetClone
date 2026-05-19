package uz.gita.paynetclone.presenter.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import uz.gita.paynetclone.usecase.payment.GetProvidersUseCase
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getProvidersUseCase: GetProvidersUseCase
) : ViewModel(), PaymentContract.ViewModel {
    private val _uiState = MutableStateFlow(PaymentContract.UiState())
    override val uiState: StateFlow<PaymentContract.UiState> = _uiState.asStateFlow()

    init {
        loadProviders()
    }

    override fun onEventDispatcher(intent: PaymentContract.Intent) {
        when (intent) {
            is PaymentContract.Intent.SetTab -> {
                _uiState.update { it.copy(activeTab = intent.tab) }
            }
            is PaymentContract.Intent.OpenPaymentCategory -> {
            }
        }
    }

    private fun loadProviders() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        getProvidersUseCase().onEach { result ->
            result.onSuccess { list ->
                _uiState.update { it.copy(isLoading = false, providers = list) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }.launchIn(viewModelScope)
    }
}
