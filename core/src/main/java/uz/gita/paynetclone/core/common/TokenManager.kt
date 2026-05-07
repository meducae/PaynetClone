package uz.gita.paynetclone.core.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val USER_PIN = stringPreferencesKey("user_pin")

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_PIN] = pin
        }
    }

    suspend fun getPin(): String? {
        return context.dataStore.data.map { prefs -> prefs[USER_PIN] }.first()
    }

    suspend fun hasPin(): Boolean {
        return !getPin().isNullOrEmpty()
    }

    suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[ACCESS_TOKEN] }.first()
    }

    suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { prefs -> prefs[REFRESH_TOKEN] }.first()
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}
