package org.michaelbel.movies.feed.event

import org.michaelbel.movies.common.mvi.Event

sealed interface FeedEvent: Event {
    data object ReselectFeed: FeedEvent
    data object OpenSearch: FeedEvent
    data object ScrollToTop: FeedEvent
    data class ShowSnackbar(val message: String, val isLong: Boolean): FeedEvent
}
