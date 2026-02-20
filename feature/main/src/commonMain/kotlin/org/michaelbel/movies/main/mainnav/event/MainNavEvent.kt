package org.michaelbel.movies.main.mainnav.event

import org.michaelbel.movies.common.mvi.Event

sealed interface MainNavEvent: Event {
    data class ShowSnackbar(val message: String): MainNavEvent
}
