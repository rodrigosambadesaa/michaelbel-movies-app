package org.michaelbel.movies.main.tabs.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface MainTabsIntent: Intent {
    data object FeedReselected: MainTabsIntent
}
