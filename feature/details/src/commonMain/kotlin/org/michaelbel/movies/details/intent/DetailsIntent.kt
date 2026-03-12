package org.michaelbel.movies.details.intent

import org.michaelbel.movies.common.mvi.Intent
import org.michaelbel.movies.persistence.database.typealiases.MovieId

sealed interface DetailsIntent: Intent {
    data object CollectAppTheme: DetailsIntent
    data object CollectNetworkStatus: DetailsIntent
    data object CollectFeatureFlags: DetailsIntent
    data object CollectAccount: DetailsIntent
    data object CollectFavorite: DetailsIntent
    data object CollectMovieDb: DetailsIntent
    data object LoadMovie: DetailsIntent
    data object BackClick: DetailsIntent
    data object GalleryClick: DetailsIntent
    data object FavoriteClick: DetailsIntent
    data class GenerateColors(val movieId: MovieId, val containerColor: Int?, val onContainerColor: Int?): DetailsIntent
}
