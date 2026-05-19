package uz.gita.paynetclone.presenter.navigation

import androidx.compose.runtime.staticCompositionLocalOf

interface AppNavigator {
    fun openPhoneRegistration()
    fun openOtpVerification(phoneNumber: String)
    fun openPin(isNewUser: Boolean)
    fun openHome()
    fun openTransfers()
    fun openSettings()
    fun openAddCard()
    fun openMyCards()
    fun openCardDetails(cardId: String)
    fun openHistory()
    fun openPayment()
    fun openServices()
    fun openIdentification()
    fun back()
}

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> {
    error("AppNavigator is not provided")
}
