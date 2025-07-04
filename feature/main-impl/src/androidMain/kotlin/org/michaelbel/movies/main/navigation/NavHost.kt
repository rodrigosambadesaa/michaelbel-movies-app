package org.michaelbel.movies.main.navigation

import androidx.navigation.NavGraphBuilder
import org.michaelbel.movies.main.mainnav.mainGraph

actual val StartDestination: Any = MainDestination

actual fun NavGraphBuilder.mainNavGraph() {
    mainGraph()
}