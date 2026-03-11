@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.color.PaletteStyle
import org.michaelbel.movies.ui.color.TonalPalettes.Companion.toTonalPalettes
import org.michaelbel.movies.ui.ktx.navigationBarStyle
import org.michaelbel.movies.ui.ktx.statusBarStyle
import org.michaelbel.movies.ui.theme.model.ComposeTheme
import kotlin.math.min

@Composable
actual fun MoviesTheme(
    themeData: ThemeData,
    theme: AppTheme,
    enableEdgeToEdge: (statusBarStyle: Any, navigationBarStyle: Any) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val seedColorPalettes = Color(themeData.seedColor).toTonalPalettes(paletteStyles.getOrElse(themeData.paletteKey) { PaletteStyle.TonalSpot })
    val paletteLightColorScheme = if (themeData.paletteColors) seedColorPalettes.paletteLightColorScheme else expressiveLightColorScheme()
    val paletteDarkColorScheme = if (themeData.paletteColors) seedColorPalettes.paletteDarkColorScheme else darkColorScheme()
    val (colorScheme, detectDarkMode) = when (themeData.appTheme) {
        AppTheme.NightNo -> {
            ComposeTheme(
                colorScheme = if (themeData.dynamicColors) dynamicLightColorScheme(context) else paletteLightColorScheme,
                detectDarkMode = false
            )
        }
        AppTheme.NightYes -> {
            ComposeTheme(
                colorScheme = if (themeData.dynamicColors) dynamicDarkColorScheme(context) else paletteDarkColorScheme,
                detectDarkMode = true
            )
        }
        AppTheme.FollowSystem -> {
            val darkTheme = isSystemInDarkTheme()
            ComposeTheme(
                colorScheme = when {
                    themeData.dynamicColors -> if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                    else -> if (darkTheme) paletteDarkColorScheme else paletteLightColorScheme
                },
                detectDarkMode = darkTheme
            )
        }
        AppTheme.Amoled -> {
            ComposeTheme(
                colorScheme = AmoledColorScheme,
                detectDarkMode = true
            )
        }
    }

    enableEdgeToEdge(
        statusBarStyle(detectDarkMode),
        navigationBarStyle(detectDarkMode)
    )

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density,
            fontScale = min(density.fontScale, 1.15F)
        )
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = MoviesShapes,
            typography = MoviesTypography
        ) {
            content()
        }
    }
}
