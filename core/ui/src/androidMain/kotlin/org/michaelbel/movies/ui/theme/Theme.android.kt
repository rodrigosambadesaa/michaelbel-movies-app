@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.ui.theme

//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.color.PaletteStyle
import org.michaelbel.movies.ui.color.TonalPalettes.Companion.toTonalPalettes
import org.michaelbel.movies.ui.ktx.context
import org.michaelbel.movies.ui.ktx.navigationBarStyle
import org.michaelbel.movies.ui.ktx.statusBarStyle
import org.michaelbel.movies.ui.theme.model.ComposeTheme

@Composable
actual fun MoviesTheme(
    themeData: ThemeData,
    theme: AppTheme,
    enableEdgeToEdge: (statusBarStyle: Any, navigationBarStyle: Any) -> Unit,
    content: @Composable () -> Unit
) {
    val seedColorPalettes = Color(themeData.seedColor).toTonalPalettes(paletteStyles.getOrElse(themeData.paletteKey) { PaletteStyle.TonalSpot })
    val paletteLightColorScheme = if (themeData.paletteColors) seedColorPalettes.paletteLightColorScheme else lightColorScheme()
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

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        shapes = MoviesShapes,
        typography = MoviesTypography
    ) {
        //CompositionLocalProvider(
        //    LocalRippleConfiguration provides moviesRippleConfiguration(
        //        color = MaterialTheme.colorScheme.primary
        //    )
        //) {
            content()
        //}
    }
}
