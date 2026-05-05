package org.michaelbel.movies.details.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface DetailsIntent: Intent {
    data object CollectNetworkStatus: DetailsIntent
    data object CollectFeatureFlags: DetailsIntent
    data object CollectAccount: DetailsIntent
    data object CollectFavorite: DetailsIntent
    data object CollectMovieDb: DetailsIntent
    data object LoadMovie: DetailsIntent
    data object BackClick: DetailsIntent
    data object GalleryClick: DetailsIntent
    data object FavoriteClick: DetailsIntent
}
