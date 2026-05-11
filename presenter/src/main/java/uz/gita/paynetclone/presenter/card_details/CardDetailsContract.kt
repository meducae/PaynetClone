package uz.gita.paynetclone.presenter.card_details

import uz.gita.paynetclone.entity.card.Card

interface CardDetailsContract {
    data class State(
        val card: Card? = null,
        val isLoading: Boolean = false,
        val isMainCard: Boolean = false,
        val remainingTransfers: Int = 1,
        val maxTransfers: Int = 1
    )

    sealed interface Intent {
        data class LoadCard(val cardId: String) : Intent
        object OnBackClicked : Intent
        object OnCopyClicked : Intent
        object OnTopUpClicked : Intent
        object OnTransferClicked : Intent
        object OnPayClicked : Intent
        object OnQrCashoutClicked : Intent
        object OnOfferClicked : Intent
        object OnTermsClicked : Intent
        data class OnMainCardChanged(val isMain: Boolean) : Intent
        object OnVerifyClicked : Intent
    }

    sealed interface SideEffect {
        object Back : SideEffect
        data class ShowToast(val message: String) : SideEffect
        object NavigateToVerify : SideEffect
        object NavigateToTopUp : SideEffect
        object NavigateToTransfer : SideEffect
        object NavigateToPay : SideEffect
    }
}
