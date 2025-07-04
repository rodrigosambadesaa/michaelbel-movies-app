package org.michaelbel.movies.feed.intent

import org.michaelbel.movies.common.mvi.Intent
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

sealed interface FeedIntent: Intent {
    data object CollectAccountPojo: FeedIntent
    data object CollectFeedView: FeedIntent
    data object CollectMovieList: FeedIntent
    data object CollectNetworkStatus: FeedIntent
    data object HideNotificationDialog: FeedIntent
    data object SubscribeNotificationsPermissionRequired: FeedIntent
    data object SettingsClick: FeedIntent
    data object SearchClick: FeedIntent
    data object AuthClick: FeedIntent
    data object AccountClick: FeedIntent
    data class MovieDetailsClick(val pagingKey: PagingKey, val movieId: MovieId): FeedIntent
}