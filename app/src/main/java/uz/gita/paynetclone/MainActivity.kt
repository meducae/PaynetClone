package uz.gita.paynetclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.screens.auth.LanguageSelectorScreen
import uz.gita.paynetclone.screens.auth.PhoneRegistrationScreen
import uz.gita.paynetclone.common.AppEnvironment

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import uz.gita.paynetclone.core.common.AuthEvent
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.screens.auth.PhoneCodeVerificationScreen
import uz.gita.paynetclone.screens.auth.PinScreen
import uz.gita.paynetclone.screens.home.HomeScreen
import uz.gita.paynetclone.screens.home.AddCardScreen
import uz.gita.paynetclone.presenter.navigation.AppNavigator
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator
import uz.gita.paynetclone.screens.profile.settings.SettingsScreen
import uz.gita.paynetclone.di.NavigationModule
import uz.gita.paynetclone.screens.card_details.CardDetailsScreen
import uz.gita.paynetclone.screens.home.MyCardsScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var authEventBus: AuthEventBus

    private val voyagerAppNavigator = VoyagerAppNavigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefsManager.init(this)
        NavigationModule.setNavigator(voyagerAppNavigator)

        authEventBus.events
            .onEach { event ->
                if (event is AuthEvent.Logout) {
                    voyagerAppNavigator.openPhoneRegistration()
                }
            }
            .launchIn(lifecycleScope)

        enableEdgeToEdge()
        setContent {
            AppEnvironment {
                val coordinator = remember { voyagerAppNavigator }
                val startScreen = remember {
                    runBlocking {
                        if (!PrefsManager.hasSelectedLanguage()) {
                            LanguageSelectorScreen()
                        } else if (tokenManager.getAccessToken().isNullOrEmpty()) {
                            PhoneRegistrationScreen()
                        } else if (tokenManager.hasPin()) {
                            PinScreen(isNewUser = false)
                        } else {
                            PinScreen(isNewUser = true)
                        }
                    }
                }
                CompositionLocalProvider(LocalAppNavigator provides coordinator) {
                    Navigator(startScreen) { nav ->
                        coordinator.navigator = nav
                        SlideTransition(nav)
                    }
                }
            }
        }
    }

    private class VoyagerAppNavigator : AppNavigator {
        var navigator: Navigator? = null

        override fun openPhoneRegistration() {
            replaceAllOnce(PhoneRegistrationScreen())
        }

        override fun openOtpVerification(phoneNumber: String) {
            pushOnce(PhoneCodeVerificationScreen(phoneNumber))
        }

        override fun openPin(isNewUser: Boolean) {
            replaceAllOnce(PinScreen(isNewUser))
        }

        override fun openHome() {
            replaceAllOnce(HomeScreen())
        }

        override fun openTransfers() {
            replaceAllOnce(uz.gita.paynetclone.screens.transfers.TransfersScreen())
        }

        override fun openSettings() {
            pushOnce(SettingsScreen())
        }

        override fun openAddCard() {
            pushOnce(AddCardScreen())
        }

        override fun openMyCards() {
            pushOnce(MyCardsScreen())
        }

        override fun openCardDetails(cardId: String) {
            pushOnce(CardDetailsScreen(cardId))
        }

        override fun openHistory() {
            replaceAllOnce(uz.gita.paynetclone.screens.history.HistoryScreen())
        }

        override fun openPayment() {
            replaceAllOnce(uz.gita.paynetclone.screens.payment.PaymentScreen())
        }

        override fun openServices() {
            replaceAllOnce(uz.gita.paynetclone.screens.services.ServicesScreen())
        }

        override fun back() {
            navigator?.pop()
        }

        private fun pushOnce(screen: Screen) {
            val nav = navigator ?: return
            if (nav.lastItem.key == screen.key) return
            nav.push(screen)
        }

        private fun replaceAllOnce(screen: Screen) {
            val nav = navigator ?: return
            if (nav.lastItem.key == screen.key) return
            nav.replaceAll(screen)
        }
    }
}
