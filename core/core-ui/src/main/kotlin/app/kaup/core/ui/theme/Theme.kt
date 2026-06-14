package app.kaup.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = BackgroundDark,
    surface = BackgroundDark,
    surfaceVariant = SurfaceDark,
    error = ErrorRedDark
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundLight,
    surface = BackgroundLight,
    error = ErrorRedLight
)

@Immutable
data class KaupCustomColors(
    val success: Color = Color.Unspecified,
    val onSuccess: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
    val onWarning: Color = Color.Unspecified
)

val LocalKaupCustomColors = staticCompositionLocalOf { KaupCustomColors() }

@Composable
fun KaupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to maintain POS branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) {
        KaupCustomColors(
            success = SuccessGreenDark,
            onSuccess = Color.Black,
            warning = WarningOrangeDark,
            onWarning = Color.Black
        )
    } else {
        KaupCustomColors(
            success = SuccessGreenLight,
            onSuccess = Color.White,
            warning = WarningOrangeLight,
            onWarning = Color.White
        )
    }

    CompositionLocalProvider(LocalKaupCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KaupTypography,
            shapes = KaupShapes,
            content = content
        )
    }
}

object KaupTheme {
    val customColors: KaupCustomColors
        @Composable
        get() = LocalKaupCustomColors.current
}
