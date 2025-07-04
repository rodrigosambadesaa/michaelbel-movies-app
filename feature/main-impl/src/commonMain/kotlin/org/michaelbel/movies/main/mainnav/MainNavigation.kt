package org.michaelbel.movies.main.mainnav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import org.michaelbel.movies.main.navigation.MainDestination

fun NavGraphBuilder.mainGraph() {
    composable<MainDestination>(
        deepLinks = listOf(
            navDeepLink { uriPattern = "movies://redirect_url?request_token={requestToken}&approved={approved}" }
        )
    ) {
        MainNavRoute()
    }
}