package uz.gita.paynetclone.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gita.paynetclone.core.utils.ThemeMode
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): ViewModel() {
    var themeMode by mutableStateOf<ThemeMode>(ThemeMode.System)
        private set

    fun updateTheme(newMode: ThemeMode) {
        themeMode = newMode
    }
}