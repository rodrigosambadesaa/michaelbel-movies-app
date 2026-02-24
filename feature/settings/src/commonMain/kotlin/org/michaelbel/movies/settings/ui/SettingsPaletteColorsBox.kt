@file:OptIn(ExperimentalFoundationApi::class)

package org.michaelbel.movies.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.settings.ui.common.SettingPaletteColor
import org.michaelbel.movies.ui.color.PaletteStyle
import org.michaelbel.movies.ui.color.TonalPalettes.Companion.toTonalPalettes
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.pagerindicator.HorizontalPagerIndicator
import org.michaelbel.movies.ui.theme.colorList
import org.michaelbel.movies.ui.theme.paletteStyles

@Composable
internal fun SettingsPaletteColorsBox(
    paletteKey: Int,
    seedColor: Int,
    onChange: (Int, Int) -> Unit
) {
    val colorsPerPage = if (isPortrait) 1 else 2
    val colorPages = remember(colorsPerPage) { colorList.chunked(colorsPerPage) }
    val pageCount = colorPages.size + 1
    val selectedColorIndex = colorList.indexOf(Color(seedColor)).run { if (this == -1) 0 else this }
    val selectedPage = if (paletteKey == ThemeData.STYLE_MONOCHROME) pageCount - 1 else selectedColorIndex / colorsPerPage
    val pagerState = rememberPagerState(initialPage = selectedPage) { pageCount }

    LaunchedEffect(selectedPage, pageCount) {
        val targetPage = selectedPage.coerceIn(0, pageCount - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) { page ->
            if (page < colorPages.size) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    colorPages[page].forEach { color ->
                        val colorArgb = color.toArgb()
                        paletteStyles.subList(ThemeData.STYLE_TONAL_SPOT, ThemeData.STYLE_MONOCHROME).forEachIndexed { index, style ->
                            SettingPaletteColor(
                                tonalPalettes = color.toTonalPalettes(style),
                                isSelected = paletteKey == index && seedColor == colorArgb,
                                onClick = { onChange(index, colorArgb) }
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SettingPaletteColor(
                        tonalPalettes = Color.Black.toTonalPalettes(PaletteStyle.Monochrome),
                        isSelected = paletteKey == ThemeData.STYLE_MONOCHROME,
                        onClick = { onChange(ThemeData.STYLE_MONOCHROME, Color.Black.toArgb()) }
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(vertical = 12.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.outlineVariant,
            indicatorWidth = 6.dp,
            indicatorHeight = 6.dp,
            spacing = 2.dp
        )
    }
}
