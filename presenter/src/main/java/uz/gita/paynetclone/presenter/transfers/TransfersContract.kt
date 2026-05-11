package uz.gita.paynetclone.presenter.transfers

import uz.gita.paynetclone.entity.card.Card
import uz.gita.paynetclone.entity.transfer.RecipientCard

interface TransfersContract {
    data class State(
        val isLoading: Boolean = false,
        val searchText: String = "",
        val selectedTab: Int = 0,
        val isSearching: Boolean = false,
        val myCards: List<Card> = emptyList(),
        val recentCards: List<RecipientCard> = emptyList(),
        val searchedRecipient: RecipientCard? = null,
        val selectedFromCard: Card? = null,
        val selectedToCard: RecipientCard? = null,
        val amount: String = "",
        val transferStep: TransferStep = TransferStep.SEARCH,
        val confirmToken: String? = null,
        val errorMessage: String? = null,
        val isCardSelectionVisible: Boolean = false
    )

    enum class TransferStep {
        SEARCH,
        DETAILS,
        CONFIRMATION,
        CONFIRM_PIN,
        CONFIRM_OTP,
        SUCCESS
    }

    sealed interface Intent {
        data class SearchChanged(val text: String) : Intent
        data class TabSelected(val index: Int) : Intent
        object OnSearchClicked : Intent
        object OnCancelSearchClicked : Intent
        data class OnRecipientSelected(val recipient: RecipientCard) : Intent
        data class OnFromCardSelected(val card: Card) : Intent
        data class AmountChanged(val amount: String) : Intent
        object OnContinueClicked : Intent
        object OnConfirmTransferClicked : Intent
        object OpenCardSelection : Intent
        object DismissCardSelection : Intent
        data class OnPinEntered(val pin: String) : Intent
        data class OnOtpEntered(val code: String) : Intent
        object OnBackClicked : Intent
        
        object OnContactClicked : Intent
        object OnQrClicked : Intent
        object OnUzumBannerClicked : Intent
        object OnIdentificationClicked : Intent
        object OnMyCardsClicked : Intent
        object OnAttoClicked : Intent
    }

    sealed interface SideEffect {
        object NavigateToContacts : SideEffect
        object NavigateToQrScanner : SideEffect
        object NavigateToUzumTransfer : SideEffect
        object NavigateToIdentification : SideEffect
        object NavigateToMyCards : SideEffect
        object NavigateToAtto : SideEffect
        data class ShowToast(val message: String) : SideEffect
    }
}
