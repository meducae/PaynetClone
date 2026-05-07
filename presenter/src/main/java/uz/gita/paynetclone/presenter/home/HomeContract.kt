package uz.gita.paynetclone.presenter.home

import uz.gita.paynetclone.entity.user.User

interface HomeContract {
    data class State(
        val isBalanceVisible: Boolean = true,
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        object ToggleBalanceVisibility : Intent
        object OnSettingsClicked : Intent
        object OnChatClicked : Intent
        object LoadProfile : Intent
        object Logout : Intent
    }

    sealed interface SideEffect {
        object NavigateToSettings : SideEffect
        object NavigateToChat : SideEffect
    }
}
