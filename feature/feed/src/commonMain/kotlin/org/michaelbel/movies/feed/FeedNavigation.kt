package org.michaelbel.movies.feed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.michaelbel.movies.feed.navigation.FeedDestination
import org.michaelbel.movies.feed.ui.FeedScreen

fun NavGraphBuilder.feedGraph() {
    composable<FeedDestination> { FeedScreen() }
}