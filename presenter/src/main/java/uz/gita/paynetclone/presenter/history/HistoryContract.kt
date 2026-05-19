package uz.gita.paynetclone.presenter.history

import kotlinx.coroutines.flow.StateFlow
import uz.gita.paynetclone.entity.transaction.Transaction

interface HistoryContract {
    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(intent: Intent)
    }

    data class UiState(
        val isLoading: Boolean = false,
        val filter: HistoryFilter = HistoryFilter.ALL,
        val transactions: List<Transaction> = emptyList(),
        val errorMessage: String? = null
    )

    enum class HistoryFilter { ALL, EXPENSES, TOPUP }

    sealed interface Intent {
        object LoadHistory : Intent
        data class SetFilter(val filter: HistoryFilter) : Intent
        object ConnectMonitoring : Intent
    }
}
