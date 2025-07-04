package org.michaelbel.movies.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import org.michaelbel.movies.ui.navigation.DetailsDestination

fun NavGraphBuilder.detailsGraph() {
    composable<DetailsDestination>(
        deepLinks = listOf(
            navDeepLink { uriPattern = "https://www.themoviedb.org/movie/{movieId}" },
            navDeepLink { uriPattern = "movies://details/{movieId}" }
        )
    ) {
        DetailsScreen()
    }
}