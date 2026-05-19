package uz.gita.paynetclone.presenter.identification

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
import uz.gita.paynetclone.entity.kyc.KycSubmitData
import uz.gita.paynetclone.usecase.kyc.SubmitKycUseCase
import javax.inject.Inject

@HiltViewModel
class IdentificationViewModel @Inject constructor(
    private val submitKycUseCase: SubmitKycUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(IdentificationContract.State())
    val state: StateFlow<IdentificationContract.State> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<IdentificationContract.SideEffect>()
    val sideEffect: SharedFlow<IdentificationContract.SideEffect> = _sideEffect.asSharedFlow()

    fun onEvent(intent: IdentificationContract.Intent) {
        when (intent) {
            is IdentificationContract.Intent.SetDocumentInfo -> {
                val currentData = _state.value.kycData ?: KycSubmitData("", "", "", "")
                _state.update {
                    it.copy(
                        kycData = currentData.copy(
                            passportSeries = intent.series,
                            passportNumber = intent.number,
                            birthDate = intent.dob
                        )
                    )
                }
                viewModelScope.launch {
                    _sideEffect.emit(IdentificationContract.SideEffect.NavigateToSelfieInstruction)
                }
            }

            is IdentificationContract.Intent.SubmitSelfie -> {
                val currentData = _state.value.kycData ?: KycSubmitData("", "", "", "")
                val finalData = currentData.copy(selfieBase64 = intent.base64)

                _state.update { it.copy(isLoading = true, error = null, kycData = finalData) }

                viewModelScope.launch {
                    submitKycUseCase(finalData).collect { result ->
                        result.onSuccess {
                            _state.update { it.copy(isLoading = false, isSubmitted = true) }
                            _sideEffect.emit(IdentificationContract.SideEffect.NavigateToSuccess)
                        }.onFailure { error ->
                            _state.update { it.copy(isLoading = false, error = error.message) }
                            _sideEffect.emit(IdentificationContract.SideEffect.ShowToast(error.message ?: "Submission failed"))
                        }
                    }
                }
            }

            IdentificationContract.Intent.Back -> {
                viewModelScope.launch {
                    _sideEffect.emit(IdentificationContract.SideEffect.NavigateBack)
                }
            }

            IdentificationContract.Intent.Finish -> {
                viewModelScope.launch {
                    _sideEffect.emit(IdentificationContract.SideEffect.FinishVerification)
                }
            }
        }
    }
}
