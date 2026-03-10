@file:OptIn(ExperimentalFoundationApi::class)

package org.michaelbel.movies.settings.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.settings.ui.common.SettingAppIcon
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.pagerindicator.HorizontalPagerIndicator

@Composable
fun SettingsAppIconsBox(
    enabledIcon: IconAlias,
    onChange: (IconAlias) -> Unit
) {
    val iconsPerPage = if (isPortrait) 4 else 8
    val iconPages = remember(iconsPerPage) { IconAlias.VALUES.chunked(iconsPerPage) }
    val selectedIconIndex = IconAlias.VALUES.indexOf(enabledIcon).run { if (this == -1) 0 else this }
    val selectedPage = selectedIconIndex / iconsPerPage
    val pagerState = rememberPagerState(initialPage = selectedPage) { iconPages.size }

    LaunchedEffect(selectedPage, iconPages.size) {
        val targetPage = selectedPage.coerceIn(0, iconPages.size - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState
        ) { page ->
            val iconsOnPage = iconPages[page]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                iconsOnPage.forEach { iconAlias ->
                    SettingAppIcon(
                        iconAlias = iconAlias,
                        isEnabled = iconAlias == enabledIcon,
                        onClick = onChange
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(vertical = 12.dp),
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.outline,
            indicatorWidth = 6.dp,
            indicatorHeight = 6.dp,
            spacing = 2.dp
        )
    }
}
