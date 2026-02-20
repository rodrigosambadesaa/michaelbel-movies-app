package org.michaelbel.movies.details.event

import org.michaelbel.movies.common.mvi.Event

sealed interface DetailsEvent: Event {
    data object CopyClick: DetailsEvent
}
