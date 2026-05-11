@file:Suppress("DEPRECATION")

package uz.gita.paynetclone.common

import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import uz.gita.paynetclone.core.utils.PrefsManager
import uz.gita.paynetclone.ui.theme.PaynetCloneTheme
import java.util.Locale

@Composable
fun AppEnvironment(content: @Composable () -> Unit) {
    val language by PrefsManager.language.collectAsState()
    val themeMode by PrefsManager.themeMode.collectAsState()

    val locale = Locale(language)
    val configuration = Configuration(LocalConfiguration.current)
    configuration.setLocale(locale)
    
    val context = LocalContext.current
    val wrappedContext = remember(context, configuration) {
        object : ContextWrapper(context) {
            private val configContext = context.createConfigurationContext(configuration)
            override fun getResources(): Resources = configContext.resources
            override fun getAssets(): AssetManager = configContext.assets
        }
    }

    CompositionLocalProvider(
        LocalContext provides wrappedContext,
        LocalConfiguration provides configuration
    ) {
        PaynetCloneTheme(themeMode = themeMode) {
            content()
        }
    }
}
