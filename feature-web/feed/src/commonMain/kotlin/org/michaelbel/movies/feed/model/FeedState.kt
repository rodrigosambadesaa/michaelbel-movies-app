package org.michaelbel.movies.feed.model

import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo

internal sealed interface FeedState {
    data object Loading: FeedState
    data class Ready(val movies: List<MoviePojo>): FeedState
    data class Error(val message: String): FeedState
}
