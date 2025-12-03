package org.michaelbel.movies.main.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface MainIntent: Intent {
    data object SearchClick: MainIntent
}