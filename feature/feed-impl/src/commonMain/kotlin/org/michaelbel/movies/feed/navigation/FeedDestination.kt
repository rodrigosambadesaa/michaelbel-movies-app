package org.michaelbel.movies.feed.navigation

import kotlinx.serialization.Serializable

@Serializable
data class FeedDestination(
    val requestToken: String? = null,
    val approved: Boolean = false
)