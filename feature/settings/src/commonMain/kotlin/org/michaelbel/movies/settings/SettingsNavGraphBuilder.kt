package org.michaelbel.movies.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import org.michaelbel.movies.settings.ui.SettingsScreen
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.ui.shortcuts.INTENT_ACTION_SETTINGS

fun NavGraphBuilder.settingsGraph() {
    composable<SettingsDestination>(
        deepLinks = listOf(navDeepLink { uriPattern = INTENT_ACTION_SETTINGS })
    ) {
        SettingsScreen()
    }
}