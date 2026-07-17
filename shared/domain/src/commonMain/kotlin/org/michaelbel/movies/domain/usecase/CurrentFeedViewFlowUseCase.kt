package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class CurrentFeedViewFlowUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, FeedView>(dispatchers.io) {

    override fun execute(params: Unit): Flow<FeedView> {
        return preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceFeedViewKey)
            .map { name -> FeedView.transform(name ?: FeedView.FeedList.toString()) }
    }
}
