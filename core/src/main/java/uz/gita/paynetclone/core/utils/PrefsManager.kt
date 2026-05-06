package uz.gita.paynetclone.core.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PrefsManager {
    private lateinit var prefs: SharedPreferences

    private val _themeMode = MutableStateFlow<ThemeMode>(ThemeMode.System)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _language = MutableStateFlow("uz")
    val language: StateFlow<String> = _language

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences("paynet_prefs", Context.MODE_PRIVATE)
        _themeMode.value = getSavedTheme()
        _language.value = getSavedLanguage()
    }

    fun setThemeMode(mode: ThemeMode) {
        val modeStr = when (mode) {
            ThemeMode.System -> "system"
            ThemeMode.Light -> "light"
            ThemeMode.Dark -> "dark"
        }
        prefs.edit().putString("theme_mode", modeStr).apply()
        _themeMode.value = mode
    }

    private fun getSavedTheme(): ThemeMode {
        return when (prefs.getString("theme_mode", "system")) {
            "light" -> ThemeMode.Light
            "dark" -> ThemeMode.Dark
            else -> ThemeMode.System
        }
    }

    fun setLanguage(langCode: String) {
        prefs.edit()
            .putString("language", langCode)
            .putBoolean("has_selected_language", true)
            .apply()
        _language.value = langCode
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)

    fun hasSelectedLanguage(): Boolean {
        return prefs.getBoolean("has_selected_language", false)
    }

    private fun getSavedLanguage(): String {
        return prefs.getString("language", "uz") ?: "uz"
    }
}
