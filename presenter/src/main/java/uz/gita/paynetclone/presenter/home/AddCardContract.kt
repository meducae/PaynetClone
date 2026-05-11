package uz.gita.paynetclone.presenter.home

import kotlinx.coroutines.flow.StateFlow

interface AddCardContract {
    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(event: Intent)
    }

    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        data class Error(val message: String) : UiState
        object Success : UiState
    }

    sealed interface Intent {
        data class AttachCard(val cardNumber: String) : Intent
        object Back : Intent
    }
}
