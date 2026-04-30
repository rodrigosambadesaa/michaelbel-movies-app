package org.michaelbel.movies.main.tabs.event

import org.jetbrains.compose.resources.StringResource
import org.michaelbel.movies.common.mvi.Event

sealed interface MainTabsEvent: Event {
    data class ShowSnackbar(val message: StringResource): MainTabsEvent
}
