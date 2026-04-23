package com.mohammed.aireok.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary                = AzulPrimarioLight,
    onPrimary              = SobreAzulPrimarioLight,
    primaryContainer       = ContenedorAzulLight,
    onPrimaryContainer     = SobreContenedorAzulLight,
    secondary              = VerdeSecundarioLight,
    onSecondary            = SobreVerdeSecundarioLight,
    secondaryContainer     = ContenedorVerdeLight,
    onSecondaryContainer   = SobreContenedorVerdeLight,
    background             = FondoLight,
    onBackground           = SobreFondoLight,
    surface                = SuperficieLight,
    onSurface              = SobreSuperficieLight,
    surfaceVariant         = VarianteSuperficieLight,
    onSurfaceVariant       = SobreVarianteLight,
    outline                = ContornoLight,
    outlineVariant         = ContornoVarianteLight,
)

private val DarkColorScheme = darkColorScheme(
    primary                = AzulPrimarioDark,
    onPrimary              = SobreAzulPrimarioDark,
    primaryContainer       = ContenedorAzulDark,
    onPrimaryContainer     = SobreContenedorAzulDark,
    secondary              = VerdeSecundarioDark,
    onSecondary            = SobreVerdeSecundarioDark,
    secondaryContainer     = ContenedorVerdeDark,
    onSecondaryContainer   = SobreContenedorVerdeDark,
    background             = FondoDark,
    onBackground           = SobreFondoDark,
    surface                = SuperficieDark,
    onSurface              = SobreSuperficieDark,
    surfaceVariant         = VarianteSuperficieDark,
    onSurfaceVariant       = SobreVarianteDark,
    outline                = ContornoDark,
    outlineVariant         = ContornoVarianteDark,
)

@Composable
fun AireOKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
