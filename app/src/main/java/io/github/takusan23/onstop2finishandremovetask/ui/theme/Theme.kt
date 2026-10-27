package io.github.takusan23.onstop2finishandremovetask.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Light = lightColorScheme(
    primary = Color(0xFF185FC6), onPrimary = Color.White,
    primaryContainer = Color(0xFFE0ECFF), onPrimaryContainer = Color(0xFF10366C),
    secondary = Color(0xFF006C64), onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F3EA), onSecondaryContainer = Color(0xFF064D45),
    background = Color(0xFFF5F8FD), surface = Color.White,
    onBackground = Color(0xFF172334), onSurface = Color(0xFF172334),
    onSurfaceVariant = Color(0xFF4B6076), surfaceVariant = Color(0xFFEAF0F8),
    outline = Color(0xFF64778F), outlineVariant = Color(0xFFC2D0E2),
    error = Color(0xFFAB302A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF65201B)
)
private val Dark = darkColorScheme(
    primary = Color(0xFFA8CAFF), onPrimary = Color(0xFF062D62),
    primaryContainer = Color(0xFF203B60), onPrimaryContainer = Color(0xFFDCEAFF),
    secondary = Color(0xFF8BDBC9), onSecondary = Color(0xFF073C34),
    secondaryContainer = Color(0xFF163F39), onSecondaryContainer = Color(0xFFB3EFDF),
    background = Color(0xFF0B1422), surface = Color(0xFF172334),
    onBackground = Color(0xFFE6EDF8), onSurface = Color(0xFFE6EDF8),
    onSurfaceVariant = Color(0xFFAFBED0), surfaceVariant = Color(0xFF26364D),
    outline = Color(0xFF8496AF), outlineVariant = Color(0xFF415671),
    error = Color(0xFFFFB4AB), onError = Color(0xFF601410),
    errorContainer = Color(0xFF522B2B), onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun OnStop2FinishAndRemoveTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = if (dynamicColor && Build.VERSION.SDK_INT >= 31) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (darkTheme) Dark else Light
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
