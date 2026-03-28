package org.michaelbel.movies.mainweb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.michaelbel.movies.detailsweb.DetailsWebScreen
import org.koin.compose.KoinApplication
import org.michaelbel.movies.feedweb.FeedWebScreen
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.interactor.di.interactorKoinModule
import org.michaelbel.movies.settingsweb.SettingsWebScreen

@Composable
fun MainWebApp() {
    KoinApplication(
        application = {
            modules(interactorKoinModule)
        }
    ) {
        var destination by remember { mutableStateOf<MainWebDestination>(MainWebDestination.Feed) }

        when (val currentDestination = destination) {
            MainWebDestination.Feed -> FeedWebScreen(
                onFaveClick = {},
                onSettingsClick = { destination = MainWebDestination.Settings },
                onMovieClick = { pagingKey, movieId ->
                    destination = MainWebDestination.Details(
                        pagingKey = pagingKey,
                        movieId = movieId
                    )
                }
            )
            MainWebDestination.Settings -> SettingsWebScreen(
                onFeedClick = { destination = MainWebDestination.Feed },
                onFaveClick = {}
            )
            is MainWebDestination.Details -> DetailsWebScreen(
                pagingKey = currentDestination.pagingKey,
                movieId = currentDestination.movieId,
                onBackClick = { destination = MainWebDestination.Feed }
            )
        }
    }
}

private sealed interface MainWebDestination {
    data object Feed: MainWebDestination
    data object Settings: MainWebDestination
    data class Details(
        val pagingKey: PagingKey,
        val movieId: MovieId
    ): MainWebDestination
}
