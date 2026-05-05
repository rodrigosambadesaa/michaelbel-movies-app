@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.main.tabs.ui

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.main.tabs.model.MainTabsModel
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.FaveDestination
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun MainTabsBottomBar(
    state: MainTabsModel,
    currentDestination: AppRoute?,
    feedDestination: FeedDestination,
    dispatch: (MainTabsIntent) -> Unit
) {
    val toggleButtonColors = ToggleButtonDefaults.toggleButtonColors()
    val navigationBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = toggleButtonColors.checkedContentColor,
        selectedTextColor = toggleButtonColors.checkedContentColor,
        indicatorColor = toggleButtonColors.checkedContainerColor,
        unselectedIconColor = toggleButtonColors.contentColor,
        unselectedTextColor = toggleButtonColors.contentColor,
        disabledIconColor = toggleButtonColors.disabledContentColor,
        disabledTextColor = toggleButtonColors.disabledContentColor
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavigationBarItem(
            selected = currentDestination == feedDestination,
            onClick = { dispatch(MainTabsIntent.FeedClick) },
            icon = {
                Icon(
                    imageVector = MoviesIcons.GridView,
                    contentDescription = stringResource(MoviesStrings.main_nav_feed)
                )
            },
            label = {
                Text(
                    text = stringResource(MoviesStrings.main_nav_feed)
                )
            },
            alwaysShowLabel = true,
            colors = navigationBarItemColors
        )

        if (state.isFaveFeatureEnabled) {
            NavigationBarItem(
                selected = currentDestination == FaveDestination,
                onClick = { dispatch(MainTabsIntent.FaveClick) },
                icon = {
                    Icon(
                        imageVector = MoviesIcons.Favorite,
                        contentDescription = stringResource(MoviesStrings.main_nav_fave)
                    )
                },
                label = {
                    Text(
                        text = stringResource(MoviesStrings.main_nav_fave)
                    )
                },
                alwaysShowLabel = true,
                colors = navigationBarItemColors
            )
        }

        NavigationBarItem(
            selected = currentDestination == SettingsDestination,
            onClick = { dispatch(MainTabsIntent.SettingsClick) },
            icon = {
                Icon(
                    imageVector = MoviesIcons.Settings,
                    contentDescription = stringResource(MoviesStrings.main_nav_settings)
                )
            },
            label = {
                Text(
                    text = stringResource(MoviesStrings.main_nav_settings)
                )
            },
            alwaysShowLabel = true,
            colors = navigationBarItemColors
        )
    }
}
