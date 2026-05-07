package org.michaelbel.movies.persistence.database.ktx

import org.michaelbel.movies.network.config.TMDB_MOVIE_URL
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo

val MoviePojo.isNotEmpty: Boolean
    get() = this != MoviePojo.Empty

val MoviePojo.url: String
    get() = "$TMDB_MOVIE_URL/$movieId"

val MoviePojo?.orEmpty: MoviePojo
    get() = this ?: MoviePojo.Empty
