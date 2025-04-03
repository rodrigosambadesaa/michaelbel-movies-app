package org.michaelbel.movies.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.michaelbel.movies.account.AccountDestination
import org.michaelbel.movies.account.accountGraph
import org.michaelbel.movies.auth.AuthDestination
import org.michaelbel.movies.auth.authGraph
import org.michaelbel.movies.details.DetailsDestination
import org.michaelbel.movies.details.detailsGraph
import org.michaelbel.movies.gallery.GalleryDestination
import org.michaelbel.movies.gallery.galleryGraph
import org.michaelbel.movies.main.navigation.StartDestination
import org.michaelbel.movies.main.navigation.mainNavGraph
import org.michaelbel.movies.search.SearchDestination
import org.michaelbel.movies.search.searchGraph
import org.michaelbel.movies.settings.SettingsDestination
import org.michaelbel.movies.settings.settingsGraph
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.navigation.BackDestination
import org.michaelbel.movies.ui.navigation.MainNavigator

@Composable
fun MainContent(
    onRequestReview: () -> Unit = {},
    onRequestUpdate: () -> Unit = {},
    navHostController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navHostController,
        startDestination = StartDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        authGraph()
        accountGraph()
        mainNavGraph(
            navigateToSearch = { navHostController.navigate(SearchDestination) },
            navigateToAuth = { navHostController.navigate(AuthDestination) },
            navigateToAccount = { navHostController.navigate(AccountDestination) },
            navigateToSettings = { navHostController.navigate(SettingsDestination) },
            navigateToDetails = { pagingKey, movieId -> navHostController.navigate(DetailsDestination(pagingKey, movieId)) },
            onRequestReview = onRequestReview,
            onRequestUpdate = onRequestUpdate
        )
        detailsGraph(
            navigateBack = navHostController::popBackStack,
            navigateToGallery = { movieId -> navHostController.navigate(GalleryDestination(movieId)) }
        )
        galleryGraph()
        searchGraph(
            navigateToDetails = { pagingKey, movieId -> navHostController.navigate(DetailsDestination(pagingKey, movieId)) }
        )
        settingsGraph(
            navigateBack = navHostController::popBackStack,
            onRequestReview = onRequestReview,
            onRequestUpdate = onRequestUpdate
        )
    }

    ObserveAsEvents(MainNavigator.destFlow) { dest ->
        when (dest) {
            is BackDestination -> navHostController.popBackStack()
            is AuthDestination -> navHostController.navigate(AuthDestination)
            is AccountDestination -> navHostController.navigate(AccountDestination)
            is SearchDestination -> navHostController.navigate(SearchDestination)
            is SettingsDestination -> navHostController.navigate(SettingsDestination)
            is DetailsDestination -> navHostController.navigate(DetailsDestination(dest.movieList, dest.movieId))
            is GalleryDestination -> navHostController.navigate(GalleryDestination(dest.movieId))
        }
    }
}