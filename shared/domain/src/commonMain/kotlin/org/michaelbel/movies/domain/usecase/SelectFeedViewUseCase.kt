@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.analytics.event.SelectFeedViewEvent
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class SelectFeedViewUseCase(
    private val preferences: MoviesPreferences,
    private val analytics: MoviesAnalytics,
    dispatchers: SharedDispatchers
): UseCase<FeedView, Unit>(dispatchers.io) {

    override suspend fun execute(feedView: FeedView) {
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceFeedViewKey, feedView.toString())
        analytics.logEvent(SelectFeedViewEvent(feedView.toString()))
    }
}
