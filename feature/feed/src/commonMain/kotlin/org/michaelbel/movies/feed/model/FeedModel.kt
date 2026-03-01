package org.michaelbel.movies.feed.model

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo

data class FeedModel(
    val accountPojo: AccountPojo = AccountPojo.Empty,
    val feedView: FeedView = FeedView.FeedList,
    val movieList: MovieList = MovieList.NowPlaying(),
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val isFeedLoading: Boolean = false, // TODO Fallback iOS
    val suggestions: List<SuggestionPojo> = emptyList(),
    val searchHistoryMovies: List<MoviePojo> = emptyList(),
    val fallbackMovies: List<MoviePojo> = emptyList() // TODO Fallback iOS
): Model
