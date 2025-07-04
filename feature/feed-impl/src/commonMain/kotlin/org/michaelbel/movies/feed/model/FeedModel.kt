package org.michaelbel.movies.feed.model

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo

data class FeedModel(
    val accountPojo: AccountPojo = AccountPojo.Empty,
    val feedView: FeedView = FeedView.FeedList,
    val movieList: MovieList = MovieList.NowPlaying(),
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable
): Model