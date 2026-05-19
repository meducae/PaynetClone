package uz.gita.paynetclone.presenter.payment

import kotlinx.coroutines.flow.StateFlow
import uz.gita.paynetclone.entity.payment.Provider

interface PaymentContract {
    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(intent: Intent)
    }

    data class UiState(
        val activeTab: PaymentTab = PaymentTab.PLACES,
        val providers: List<Provider> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    enum class PaymentTab {
        PLACES, TEMPLATES
    }

    sealed interface Intent {
        data class SetTab(val tab: PaymentTab) : Intent
        data class OpenPaymentCategory(val categoryId: String) : Intent
    }
}
