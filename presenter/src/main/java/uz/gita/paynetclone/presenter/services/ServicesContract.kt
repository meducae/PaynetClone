package uz.gita.paynetclone.presenter.services

import kotlinx.coroutines.flow.StateFlow

interface ServicesContract {
    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(intent: Intent)
    }

    data class UiState(
        val isLoading: Boolean = false
    )

    sealed interface Intent {
        data class OpenService(val serviceId: String) : Intent
    }
}
