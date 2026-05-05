package org.michaelbel.movies.details.ktx

import androidx.compose.runtime.Composable
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.persistence.database.entity.pojo.MoviePojo
import org.michaelbel.movies.persistence.database.ktx.url

val ScreenState.Content<*>.movie: MoviePojo
    get() = data as MoviePojo

val ScreenState.toolbarTitle: String
    @Composable get() = when (this) {
        is ScreenState.Loading -> ""
        is ScreenState.Content<*> -> movie.title
        is ScreenState.Failure -> ""
    }

val ScreenState.movieUrl: String?
    get() = if (this is ScreenState.Content<*>) movie.url else null
