package uz.gita.paynetclone.feature.home.presentation

interface HomeContract {
    data class State(
        val isBalanceVisible: Boolean = true
    )

    sealed interface Intent {
        object ToggleBalanceVisibility : Intent
        object OnSettingsClicked : Intent
        object OnChatClicked : Intent
    }

    sealed interface SideEffect {
        object NavigateToSettings : SideEffect
        object NavigateToChat : SideEffect
    }
}
