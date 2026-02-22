package org.michaelbel.movies.main.mainnav.event

import org.jetbrains.compose.resources.StringResource
import org.michaelbel.movies.common.mvi.Event

sealed interface MainNavEvent: Event {
    data class ShowSnackbar(val message: StringResource): MainNavEvent
}
