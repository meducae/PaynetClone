package uz.gita.paynetclone.presenter.auth

import android.util.Log
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
import uz.gita.paynetclone.usecase.auth.SendOtpUseCase
import uz.gita.paynetclone.usecase.auth.VerifyOtpUseCase
import uz.gita.paynetclone.core.utils.PrefsManager
import javax.inject.Inject

data class AuthState(
    val phone: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface AuthIntent {
    data class PhoneChanged(val phone: String) : AuthIntent
    data class OtpChanged(val otp: String) : AuthIntent
    data object SendOtpClicked : AuthIntent
    data object VerifyOtpClicked : AuthIntent
}

sealed interface AuthSideEffect {
    data object NavigateToOtpScreen : AuthSideEffect
    data object NavigateToHome : AuthSideEffect
    data class NavigateToPinScreen(val isNewUser: Boolean) : AuthSideEffect
    data class ShowError(val message: String) : AuthSideEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AuthSideEffect>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<AuthSideEffect> = _sideEffect.asSharedFlow()

    fun onEvent(intent: AuthIntent) {
        Log.d("TTT", "onEvent: ${AuthIntent.SendOtpClicked}")
        when (intent) {
            is AuthIntent.PhoneChanged -> _state.update { it.copy(phone = intent.phone, error = null) }
            is AuthIntent.OtpChanged -> _state.update { it.copy(otp = intent.otp, error = null) }
            is AuthIntent.SendOtpClicked -> sendOtp()
            is AuthIntent.VerifyOtpClicked -> verifyOtp()
        }
    }

    private fun sendOtp() {
        if (_state.value.isLoading) return
        val currentPhone = _state.value.phone
        if (currentPhone.isBlank()) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            sendOtpUseCase(currentPhone)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(AuthSideEffect.NavigateToOtpScreen)
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Failed to send OTP"
                    _state.update { it.copy(isLoading = false, error = message) }
                    _sideEffect.emit(AuthSideEffect.ShowError(message))
                }
        }
    }

    private fun verifyOtp() {
        if (_state.value.isLoading) return
        val currentState = _state.value
        if (currentState.otp.isBlank()) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            verifyOtpUseCase(currentState.phone, currentState.otp)
                .onSuccess { authResult ->
                    _state.update { it.copy(isLoading = false) }
                    
                    PrefsManager.saveTokens(authResult.accessToken, authResult.refreshToken)

                    if (authResult.isNewUser) {
                        _sideEffect.emit(AuthSideEffect.NavigateToPinScreen(isNewUser = true))
                    } else {
                        _sideEffect.emit(AuthSideEffect.NavigateToPinScreen(isNewUser = false))
                    }
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Invalid OTP"
                    _state.update { it.copy(isLoading = false, error = message) }
                    _sideEffect.emit(AuthSideEffect.ShowError(message))
                }
        }
    }
}
