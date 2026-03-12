package org.michaelbel.movies.main.tabs.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface MainTabsIntent: Intent {
    data object CollectFaveFeatureEnabled: MainTabsIntent
    data object CollectAuthorizedState: MainTabsIntent
    data object FeedClick: MainTabsIntent
    data object FaveClick: MainTabsIntent
    data object SettingsClick: MainTabsIntent
    data class HandleRedirect(val requestToken: String?, val approved: Boolean?): MainTabsIntent
    data class AuthorizeAccount(val requestToken: String): MainTabsIntent
}
