package uz.gita.paynetclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.presenter.auth.LanguageSelectorScreen
import uz.gita.paynetclone.presenter.auth.PhoneRegistrationScreen
import uz.gita.paynetclone.presenter.common.AppEnvironment

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import uz.gita.paynetclone.core.common.AuthEvent
import uz.gita.paynetclone.core.common.AuthEventBus
import uz.gita.paynetclone.core.common.TokenManager
import uz.gita.paynetclone.presenter.auth.PhoneCodeVerificationScreen
import uz.gita.paynetclone.presenter.auth.PinScreen
import uz.gita.paynetclone.presenter.home.AddCardScreen
import uz.gita.paynetclone.presenter.home.HomeScreen
import uz.gita.paynetclone.presenter.navigation.AppNavigator
import uz.gita.paynetclone.presenter.navigation.LocalAppNavigator
import uz.gita.paynetclone.presenter.profile.settings.SettingsScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var authEventBus: AuthEventBus

    private val appNavigator = VoyagerAppNavigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefsManager.init(this)

        authEventBus.events
            .onEach { event ->
                if (event is AuthEvent.Logout) {
                    appNavigator.openPhoneRegistration()
                }
            }
            .launchIn(lifecycleScope)

        enableEdgeToEdge()
        setContent {
            AppEnvironment {
                val coordinator = remember { appNavigator }
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
            navigator?.replaceAll(PhoneRegistrationScreen())
        }

        override fun openOtpVerification(phoneNumber: String) {
            navigator?.push(PhoneCodeVerificationScreen(phoneNumber))
        }

        override fun openPin(isNewUser: Boolean) {
            navigator?.replaceAll(PinScreen(isNewUser))
        }

        override fun openHome() {
            navigator?.replaceAll(HomeScreen())
        }

        override fun openSettings() {
            navigator?.push(SettingsScreen())
        }

        override fun openAddCard() {
            navigator?.push(AddCardScreen())
        }

        override fun back() {
            navigator?.pop()
        }
    }
}
