package uz.gita.paynetclone.presenter.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import uz.gita.paynetclone.usecase.transaction.GetTransactionsUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel(), HistoryContract.ViewModel {
    private val _uiState = MutableStateFlow(HistoryContract.UiState())
    override val uiState: StateFlow<HistoryContract.UiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    override fun onEventDispatcher(intent: HistoryContract.Intent) {
        when (intent) {
            is HistoryContract.Intent.LoadHistory -> loadTransactions()
            is HistoryContract.Intent.SetFilter -> {
                _uiState.update { it.copy(filter = intent.filter) }
                loadTransactions()
            }
            is HistoryContract.Intent.ConnectMonitoring -> {}
        }
    }

    private fun loadTransactions() {
        val type = when (_uiState.value.filter) {
            HistoryContract.HistoryFilter.ALL -> null
            HistoryContract.HistoryFilter.EXPENSES -> "TRANSFER_OUT"
            HistoryContract.HistoryFilter.TOPUP -> "TRANSFER_IN"
        }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        getTransactionsUseCase(type = type).onEach { result ->
            result.onSuccess { res ->
                _uiState.update { it.copy(isLoading = false, transactions = res.data) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }.launchIn(viewModelScope)
    }
}
