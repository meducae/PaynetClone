package uz.gita.paynetclone.presenter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import uz.gita.paynetclone.core.utils.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PaynetGreen,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    surfaceDim = DarkSurfaceTintBackground,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    surfaceTint = DarkBackgroundItem,
    secondary = DarkTextGray,
    error = RedExit,
    inversePrimary = WarningText,
)

private val LightColorScheme = lightColorScheme(
    primary = PaynetGreen,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    surfaceDim = LightSurfaceTintBackground,
    onSurface = LightOnSurface,
    surfaceTint = LightBackgroundItem,
    surfaceVariant = LightSurfaceVariant,
    secondary = LightTextGray,
    inversePrimary = WarningText,
    error = RedExit
)

@Composable
fun PaynetCloneTheme(
    themeMode: ThemeMode = ThemeMode.System,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
