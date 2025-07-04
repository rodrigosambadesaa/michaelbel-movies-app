package org.michaelbel.movies.main.navigation

import androidx.navigation.NavGraphBuilder
import org.michaelbel.movies.feed.feedGraph
import org.michaelbel.movies.feed.navigation.FeedDestination

actual val StartDestination: Any = FeedDestination()

actual fun NavGraphBuilder.mainNavGraph() {
    feedGraph()
}