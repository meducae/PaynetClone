package uz.gita.paynetclone.presenter.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.core.utils.ThemeMode
import uz.gita.paynetclone.entity.user.User
import uz.gita.paynetclone.entity.kyc.KycStatus
import uz.gita.paynetclone.usecase.auth.LogoutUseCase
import uz.gita.paynetclone.usecase.kyc.GetKycStatusUseCase
import uz.gita.paynetclone.usecase.user.GetProfileUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getKycStatusUseCase: GetKycStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager,
    private val authEventBus: AuthEventBus
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadProfile()
        loadKycStatus()
    }

    fun updateTheme(newMode: ThemeMode) {
        _state.update { it.copy(themeMode = newMode) }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            tokenManager.clearTokens()
            authEventBus.logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getProfileUseCase()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message) }
                }
        }
    }

    private fun loadKycStatus() {
        viewModelScope.launch {
            getKycStatusUseCase().collect { result ->
                result.onSuccess { status ->
                    _state.update { it.copy(kycStatus = status) }
                }.onFailure { throwable ->
                    // Silently fail or log, don't block profile loading
                }
            }
        }
    }
}

data class SettingsState(
    val user: User? = null,
    val kycStatus: KycStatus? = null,
    val themeMode: ThemeMode = ThemeMode.System,
    val isLoading: Boolean = false,
    val error: String? = null
)
