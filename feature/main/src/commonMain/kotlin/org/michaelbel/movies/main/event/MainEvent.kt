package org.michaelbel.movies.main.event

import org.michaelbel.movies.common.mvi.Event

sealed interface MainEvent: Event {
    data object OpenFeed: MainEvent
    data object OpenFave: MainEvent
    data object OpenSettings: MainEvent
    data object BiometricAuthenticate: MainEvent
    data object BiometricCancel: MainEvent
}
