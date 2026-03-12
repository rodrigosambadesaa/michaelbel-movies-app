package org.michaelbel.movies.fave.model

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.network.connectivity.NetworkStatus

data class FaveModel(
    val feedView: FeedView = FeedView.FeedList,
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val isPageFailureButtonVisible: Boolean = false,
    val isFeedLoading: Boolean = false
): Model
