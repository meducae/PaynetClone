package uz.gita.paynetclone.presenter.home

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
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.usecase.auth.LogoutUseCase
import uz.gita.paynetclone.usecase.user.GetProfileUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager,
    private val authEventBus: AuthEventBus
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeContract.SideEffect>()
    val sideEffect: SharedFlow<HomeContract.SideEffect> = _sideEffect.asSharedFlow()

    init {
        onEvent(HomeContract.Intent.LoadProfile)
    }

    fun onEvent(event: HomeContract.Intent) {
        when(event) {
            HomeContract.Intent.ToggleBalanceVisibility -> {
                _state.update { it.copy(isBalanceVisible = !it.isBalanceVisible) }
            }
            HomeContract.Intent.OnSettingsClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToSettings)
                }
            }
            HomeContract.Intent.OnChatClicked -> {
                viewModelScope.launch {
                    _sideEffect.emit(HomeContract.SideEffect.NavigateToChat)
                }
            }
            HomeContract.Intent.LoadProfile -> {
                loadProfile()
            }
            HomeContract.Intent.Logout -> {
                logout()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            tokenManager.clearTokens()
            authEventBus.logout()
        }
    }

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getProfileUseCase()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }
}
