package uz.gita.paynetclone.presenter.identification

import uz.gita.paynetclone.entity.kyc.KycSubmitData

interface IdentificationContract {
    sealed interface Intent {
        data class SetDocumentInfo(val series: String, val number: String, val dob: String) : Intent
        data class SubmitSelfie(val base64: String) : Intent
        object Back : Intent
        object Finish : Intent
    }

    data class State(
        val isLoading: Boolean = false,
        val isSubmitted: Boolean = false,
        val error: String? = null,
        val kycData: KycSubmitData? = null
    )

    sealed interface SideEffect {
        object NavigateBack : SideEffect
        object NavigateToSelfieInstruction : SideEffect
        object NavigateToCamera : SideEffect
        object NavigateToSuccess : SideEffect
        object FinishVerification : SideEffect
        data class ShowToast(val message: String) : SideEffect
    }
}
