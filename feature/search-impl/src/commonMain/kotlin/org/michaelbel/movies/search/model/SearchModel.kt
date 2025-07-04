package org.michaelbel.movies.search.model

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.network.connectivity.NetworkStatus
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo

data class SearchModel(
    val suggestions: List<SuggestionPojo> = emptyList(),
    val searchHistoryMovies: List<MoviePojo> = emptyList(),
    val feedView: FeedView = FeedView.FeedList,
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val query: String = ""
): Model