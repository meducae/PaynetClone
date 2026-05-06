package uz.gita.paynetclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import dagger.hilt.android.AndroidEntryPoint
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.feature.home.presentation.HomeScreen
import uz.gita.paynetclone.feature.profile.presentation.LanguageSelectorScreen
import uz.gita.paynetclone.screens.auth.PhoneRegistrationScreen
import uz.gita.paynetclone.utils.AppEnvironment

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefsManager.init(this)
        enableEdgeToEdge()
        setContent {
            AppEnvironment {
                val startScreen = if (PrefsManager.hasSelectedLanguage()) {
                    PhoneRegistrationScreen()
                } else {
                    LanguageSelectorScreen()
                }
                Navigator(startScreen) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}


