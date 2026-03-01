package org.michaelbel.movies.ui.navigation

import kotlinx.serialization.Serializable
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

@Serializable
sealed interface AppRoute

@Serializable
data object AuthDestination: AppRoute

@Serializable
data object AccountDestination: AppRoute

@Serializable
data object SettingsDestination: AppRoute

@Serializable
data object NotifyDestination: AppRoute

@Serializable
data class MainDestination(
    val requestToken: String? = null,
    val approved: Boolean? = null
): AppRoute

@Serializable
data class FeedDestination(
    val requestToken: String? = null,
    val approved: Boolean = false
): AppRoute

@Serializable
data class DetailsDestination(
    val movieList: PagingKey?,
    val movieId: MovieId
): AppRoute

@Serializable
data class GalleryDestination(
    val movieId: MovieId
): AppRoute
