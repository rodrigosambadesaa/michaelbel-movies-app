@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.theme.AppTheme

private val amoledSurface = Color(0xFF141414)

private val WebAmoledColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.White,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    inversePrimary = amoledSurface,
    secondary = Color.White,
    onSecondaryContainer = Color.White,
    surfaceTint = amoledSurface,
    onSurface = Color.White,
    surfaceVariant = amoledSurface,
    onSurfaceVariant = Color.White,
    error = Color.White,
    outline = Color.White
)

private val WebShapes = Shapes(
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun AppTheme(
    themeData: ThemeData = ThemeData.Default,
    content: @Composable () -> Unit
) {
    val dark = darkColorScheme()
    val light = lightColorScheme()

    val colorScheme = when (themeData.appTheme) {
        AppTheme.NightNo -> light
        AppTheme.NightYes -> dark
        AppTheme.FollowSystem -> if (isSystemInDarkTheme()) dark else light
        AppTheme.Amoled -> WebAmoledColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        shapes = WebShapes,
        content = content
    )
}
