package org.michaelbel.movies.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import org.michaelbel.movies.ui.navigation.SearchDestination
import org.michaelbel.movies.search.ui.SearchScreen
import org.michaelbel.movies.ui.shortcuts.INTENT_ACTION_SEARCH

fun NavGraphBuilder.searchGraph() {
    composable<SearchDestination>(
        deepLinks = listOf(
            navDeepLink { uriPattern = INTENT_ACTION_SEARCH }
        )
    ) {
        SearchScreen()
    }
}