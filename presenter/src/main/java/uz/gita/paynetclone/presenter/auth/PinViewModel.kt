package uz.gita.paynetclone.presenter.auth

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
import uz.gita.paynetclone.usecase.auth.SetPinUseCase
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.presenter.common.UiText
import uz.gita.paynetclone.presenter.R
import javax.inject.Inject

enum class PinMode {
    CREATE, CONFIRM, ENTER
}

data class PinState(
    val pin: String = "",
    val confirmPin: String = "",
    val mode: PinMode = PinMode.ENTER,
    val error: UiText? = null,
    val isLoading: Boolean = false
)

sealed interface PinIntent {
    data class OnNumberEntered(val number: String) : PinIntent
    data object OnDeleteClicked : PinIntent
    data object OnForgotClicked : PinIntent
    data object OnBiometricClicked : PinIntent
}

sealed interface PinSideEffect {
    data object NavigateToHome : PinSideEffect
    data class ShowError(val message: UiText) : PinSideEffect
}

@HiltViewModel
class PinViewModel @Inject constructor(
    private val setPinUseCase: SetPinUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(PinState())
    val state: StateFlow<PinState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<PinSideEffect>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<PinSideEffect> = _sideEffect.asSharedFlow()

    fun setInitialMode(isNewUser: Boolean) {
        _state.update { it.copy(mode = if (isNewUser) PinMode.CREATE else PinMode.ENTER) }
    }

    fun onEvent(intent: PinIntent) {
        when (intent) {
            is PinIntent.OnNumberEntered -> handleNumberEntered(intent.number)
            is PinIntent.OnDeleteClicked -> handleDelete()
            is PinIntent.OnForgotClicked -> { /* Handle forgot PIN */ }
            is PinIntent.OnBiometricClicked -> { /* Handle Biometric */ }
        }
    }

    private fun handleNumberEntered(number: String) {
        val currentState = _state.value
        if (currentState.isLoading) return

        if (currentState.pin.length < 4) {
            val newPin = currentState.pin + number
            _state.update { it.copy(pin = newPin, error = null) }

            if (newPin.length == 4) {
                processCompletedPin(newPin)
            }
        }
    }

    private fun processCompletedPin(enteredPin: String) {
        val currentState = _state.value
        when (currentState.mode) {
            PinMode.CREATE -> {
                _state.update { it.copy(mode = PinMode.CONFIRM, confirmPin = enteredPin, pin = "") }
            }
            PinMode.CONFIRM -> {
                if (enteredPin == currentState.confirmPin) {
                    submitPin(enteredPin)
                } else {
                    _state.update { it.copy(pin = "", error = UiText.StringResource(R.string.error_pin_mismatch)) }
                }
            }
            PinMode.ENTER -> {
                verifyLocalPin(enteredPin)
            }
        }
    }

    private fun verifyLocalPin(enteredPin: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val savedPin = tokenManager.getPin()
            if (savedPin == enteredPin) {
                _state.update { it.copy(isLoading = false) }
                _sideEffect.emit(PinSideEffect.NavigateToHome)
            } else {
                _state.update { it.copy(isLoading = false, pin = "", error = UiText.StringResource(R.string.error_wrong_pin)) }
            }
        }
    }

    private fun submitPin(pin: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            setPinUseCase(pin)
                .onSuccess {
                    tokenManager.savePin(pin)
                    _state.update { it.copy(isLoading = false) }
                    _sideEffect.emit(PinSideEffect.NavigateToHome)
                }
                .onFailure { throwable ->
                    val errorMessage = throwable.message ?: "Failed to set PIN"
                    _state.update { it.copy(isLoading = false, pin = "", error = UiText.DynamicString(errorMessage)) }
                    _sideEffect.emit(PinSideEffect.ShowError(UiText.DynamicString(errorMessage)))
                }
        }
    }

    private fun handleDelete() {
        _state.update { 
            if (it.pin.isNotEmpty()) {
                it.copy(pin = it.pin.dropLast(1), error = null)
            } else it
        }
    }
}
